package com.cyneck.workflow.utils;

import com.cyneck.workflow.common.FlowableConstant;
import com.cyneck.workflow.model.Node;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.impl.de.odysseus.el.ExpressionFactoryImpl;
import org.flowable.common.engine.impl.de.odysseus.el.util.SimpleContext;
import org.flowable.common.engine.impl.javax.el.ExpressionFactory;
import org.flowable.common.engine.impl.javax.el.ValueExpression;
import org.flowable.common.engine.impl.util.CollectionUtil;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>Description: 流程工具类</p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/12 15:39
 **/
@Slf4j
public class FlowableUtils {

    public static UserTask getNextNode(RepositoryService repositoryService,
                                RuntimeService runtimeService,
                                TaskService taskService,
                                String processInstanceId) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        List<org.flowable.task.api.Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();
        org.flowable.task.api.Task task = null;
        if (CollectionUtil.isNotEmpty(taskList)) {
            task = taskList.get(0);
        }
        // 逻辑分支, 指定正向表达式
        String forwardCondition = "${pass==true}";
        UserTask nextUserTask = null;
//        //查询流程图
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        Optional<Process> processOptional = bpmnModel.getProcesses().stream().findFirst();
        if (processOptional.isPresent()) {
            // 开始节点
            Optional<FlowElement> startFlowElementOptional = processOptional.get().getFlowElements().stream().filter(s -> s instanceof StartEvent).findFirst();
            // 结束节点
            Optional<FlowElement> endFlowElementOptional = processOptional.get().getFlowElements().stream().filter(s -> s instanceof EndEvent).findFirst();
            // 边集合
            List<FlowElement> flowElementList = processOptional.get().getFlowElements().stream().filter(s -> s instanceof SequenceFlow).collect(Collectors.toList());
            if (endFlowElementOptional.isPresent() && startFlowElementOptional.isPresent() && flowElementList.size() >= 1) {
                // 至少符合一个最简单的流程图的元素要求
                StartEvent startFlowElement = (StartEvent) startFlowElementOptional.get();
                EndEvent endFlowElement = (EndEvent) endFlowElementOptional.get();

                // 定义临时节点变量
                List<FlowElement> allNodeList = (List<FlowElement>) processOptional.get().getFlowElements();

                List<SequenceFlow> sequenceFlowList = new ArrayList<>();
                List<Node> nodeList = new ArrayList<>();
                HashMap<String, Node> nodeDict = new HashMap<>();
                HashMap<String, Integer> inDegreeDict = new HashMap<>();
                for (FlowElement flowElement : allNodeList) {
                    Node node = new Node();
                    if (flowElement instanceof StartEvent) {
                        node.setName(flowElement.getName());
                        node.setId(flowElement.getId());
                        List<Node> nodesIn = ((StartEvent) flowElement).getIncomingFlows().stream().map(s -> {
                            Node temp = new Node();
                            temp.setId(s.getSourceFlowElement().getId());
                            temp.setName(s.getSourceFlowElement().getName());
                            return temp;
                        }).collect(Collectors.toList());
                        List<Node> nodesOut = new ArrayList<>();
                        for (SequenceFlow tmpSequenceFLow : ((StartEvent) flowElement).getOutgoingFlows()) {
                            Node temp = new Node();
                            if (tmpSequenceFLow.getTargetFlowElement() instanceof Gateway) {
                                List<SequenceFlow> nextFLow = ((Gateway) tmpSequenceFLow.getTargetFlowElement()).getOutgoingFlows();
                                // 下一个为排他网关情况。跳过，直接作为连接项
                                // 多层网关情况不考虑
                                List<Node> nextNodeList = nextFLow.stream().map(p -> {
                                    Node tempNext = new Node();
                                    tempNext.setId(p.getTargetFlowElement().getId());
                                    tempNext.setName(p.getTargetFlowElement().getName());
                                    return tempNext;
                                }).collect(Collectors.toList());
                                nodesOut = nextNodeList;
                                break;
                            } else {
                                temp.setId(tmpSequenceFLow.getTargetFlowElement().getId());
                                temp.setName(tmpSequenceFLow.getTargetFlowElement().getName());
                            }
                            nodesOut.add(temp);
                        }
                        node.setInComingFLows(nodesIn);
                        node.setOutGoingFLows(nodesOut);
                        nodeList.add(node);
                        nodeDict.put(node.getId(), node);
                    } else if (flowElement instanceof EndEvent) {
                        node.setName(flowElement.getName());
                        node.setId(flowElement.getId());
                        List<Node> nodesIn = ((EndEvent) flowElement).getIncomingFlows().stream().map(s -> {
                            Node temp = new Node();
                            temp.setId(s.getSourceFlowElement().getId());
                            temp.setName(s.getSourceFlowElement().getName());
                            return temp;
                        }).collect(Collectors.toList());
                        List<Node> nodesOut = new ArrayList();
                        for (SequenceFlow tmpSequenceFLow : ((EndEvent) flowElement).getOutgoingFlows()) {
                            Node temp = new Node();
                            if (tmpSequenceFLow.getTargetFlowElement() instanceof Gateway) {
                                List<SequenceFlow> nextFLow = ((Gateway) tmpSequenceFLow.getTargetFlowElement()).getOutgoingFlows();
                                // 下一个为排他网关情况。跳过，直接作为连接项
                                // 多层网关情况不考虑
                                List<Node> nextNodeList = nextFLow.stream().map(p -> {
                                    Node tempNext = new Node();
                                    tempNext.setId(p.getTargetFlowElement().getId());
                                    tempNext.setName(p.getTargetFlowElement().getName());
                                    return tempNext;
                                }).collect(Collectors.toList());
                                nodesOut = nextNodeList;
                                break;
                            } else {
                                temp.setId(tmpSequenceFLow.getTargetFlowElement().getId());
                                temp.setName(tmpSequenceFLow.getTargetFlowElement().getName());
                            }
                            nodesOut.add(temp);
                        }
                        node.setInComingFLows(nodesIn);
                        node.setOutGoingFLows(nodesOut);
                        nodeList.add(node);
                        nodeDict.put(node.getId(), node);
                    } else if (flowElement instanceof UserTask) {
                        node.setName(flowElement.getName());
                        node.setId(flowElement.getId());
                        List<Node> nodesIn = ((UserTask) flowElement).getIncomingFlows().stream().map(s -> {
                            Node temp = new Node();
                            temp.setId(s.getSourceFlowElement().getId());
                            temp.setName(s.getSourceFlowElement().getName());
                            return temp;
                        }).collect(Collectors.toList());
                        List<Node> nodesOut = new ArrayList();
                        for (SequenceFlow tmpSequenceFLow : ((UserTask) flowElement).getOutgoingFlows()) {
                            Node temp = new Node();
                            if (tmpSequenceFLow.getTargetFlowElement() instanceof Gateway) {
                                List<SequenceFlow> nextFLow = ((Gateway) tmpSequenceFLow.getTargetFlowElement()).getOutgoingFlows();
                                // 下一个为排他网关情况。跳过，直接作为连接项
                                // 多层网关情况不考虑
                                List<Node> nextNodeList = nextFLow.stream().map(p -> {
                                    Node tempNext = new Node();
                                    tempNext.setId(p.getTargetFlowElement().getId());
                                    tempNext.setName(p.getTargetFlowElement().getName());
                                    return tempNext;
                                }).collect(Collectors.toList());
                                nodesOut = nextNodeList;
                                break;
                            } else {
                                temp.setId(tmpSequenceFLow.getTargetFlowElement().getId());
                                temp.setName(tmpSequenceFLow.getTargetFlowElement().getName());
                            }
                            nodesOut.add(temp);
                        }
                        node.setInComingFLows(nodesIn);
                        node.setOutGoingFLows(nodesOut);
                        nodeList.add(node);
                        nodeDict.put(node.getId(), node);
                    } else if (flowElement instanceof SequenceFlow) {
                        sequenceFlowList.add((SequenceFlow) flowElement);
                    } else {
                        //ignore
                    }
                }

                // 模拟顺序流程，从开始节点开始.深度遍历
                List<Node> rested = nodeList;
                Stack<Node> stack = new Stack();

                // 当前顶点
                AtomicReference<Node> currentNode = new AtomicReference<>();

                Node startNode = nodeDict.get(startFlowElement.getId());
                currentNode.set(startNode);
                stack.push(startNode);
                rested.removeIf(s -> s.getId().equals(startNode.getId()));

//                dfs(nodeDict, currentNode.get()); // 图递归遍历


                int count = 0;   //在最大规模是rested.size() * 2
                Boolean nextFlag = Boolean.FALSE; // 方向向后为true
                Node current = startNode; // 方向向后为true
                Node next = null; // 下一个节点
                while (!rested.isEmpty() && nextFlag == Boolean.FALSE && count < rested.size() * 2) {
                    count++;
                    if (CollectionUtils.isEmpty(currentNode.get().getOutGoingFLows())) {
                        Node lastNode = stack.pop();
                        currentNode.set(lastNode);
                    }
                    for (Node vertex : currentNode.get().getOutGoingFLows()) {
                        Node childNode = nodeDict.get(vertex.getId());
                        log.info("【child】:{}", childNode.getName());
                        Optional<Node> optionalNode = rested.stream().filter(s -> s.getId().equals(childNode.getId())).findFirst();
                        if (optionalNode.isPresent() && !childNode.getId().equals(task.getTaskDefinitionKey())) {
                            Optional<SequenceFlow> childSequenceFlowOpt = sequenceFlowList.stream().filter(s -> s.getTargetRef().equals(childNode.getId())).findFirst();
                            String expressFormat = "";
                            if (childSequenceFlowOpt.isPresent()) {
                                expressFormat = childSequenceFlowOpt.get().getConditionExpression();
                            }
                            if (StringUtils.isBlank(expressFormat)) {
                                rested.removeIf(s -> s.getId().equals(childNode.getId()));
                                if (current.getOutGoingFLows().stream().anyMatch(s -> s.getId().equals(childNode.getId()))) {
                                    next = childNode;
                                }
                                if (!CollectionUtils.isEmpty(childNode.getOutGoingFLows())) {
                                    stack.push(childNode);
                                    currentNode.set(childNode);
                                }
                                if (childNode.getId().equals(endFlowElement.getId())) {
                                    stack.push(childNode);
                                    currentNode.set(childNode);
                                    nextFlag = Boolean.TRUE;
                                }
                                break;
                            } else {
                                // 逻辑分支判断表达式不为空
                                if (forwardCondition.equals(expressFormat)) {
                                    // 正向
                                    rested.removeIf(s -> s.getId().equals(childNode.getId()));
                                    if (current.getOutGoingFLows().stream().anyMatch(s -> s.getId().equals(childNode.getId()))) {
                                        next = childNode;
                                    }
                                    if (!CollectionUtils.isEmpty(childNode.getOutGoingFLows())) {
                                        stack.push(childNode);
                                        currentNode.set(childNode);
                                    }
                                    if (childNode.getId().equals(endFlowElement.getId())) {
                                        stack.push(childNode);
                                        currentNode.set(childNode);
                                        nextFlag = Boolean.TRUE;
                                    }
                                }
                            }
                        }
                        if (childNode.getId().equals(task.getTaskDefinitionKey())) {
                            // 必然先执行
                            rested.removeIf(s -> s.getId().equals(childNode.getId()));
                            stack.push(childNode);
                            currentNode.set(childNode);
                            log.info("【当前节点】：{}", childNode.getName());
                            current = childNode;
                            break;
                        }

                    }
                }
                if (next == null) {
                    log.error("无法判断下一个节点");
                } else {
                    Node finalNext = next;
                    Optional<FlowElement> nextUserTaskOpt = allNodeList.stream().filter(s -> s.getId().equals(finalNext.getId())).findFirst();
                    if (nextUserTaskOpt.isPresent() && nextUserTaskOpt.get() instanceof UserTask) {
                        nextUserTask = (UserTask) nextUserTaskOpt.get();
                    }
                    log.info("【下一个节点:】{}", next.getName());
                }
            }
        }
//        System.out.println(bpmnModel);
        return nextUserTask;
    }

    public void dfs(HashMap<String, Node> nodeDict, Node currentNode) {
        int time = 0;
        List<Node> outs = new ArrayList<>();
        for (Node vertex : currentNode.getOutGoingFLows()) {
            Node u = nodeDict.get(vertex.getId());
            outs.add(u);
            if (u.getColor().equals("white")) {
                dfs_visit(time, nodeDict, u);
            }
        }
        currentNode.setOutGoingFLows(outs);
    }


    private void dfs_visit(int time, HashMap<String, Node> nodeDict, Node node) {
        time = time + 1;
        node.setDiscoveryTime(time);
        node.setColor("gray");
        List<Node> ins = new ArrayList<>();
        for (Node v : node.getOutGoingFLows()) {
            Node nodeV = nodeDict.get(v.getId());
            ins.add(nodeV);
            if (nodeV.getColor().equals("white")) {
                nodeV.setParent(node);
                dfs_visit(time, nodeDict, nodeV);
            }
        }
        node.setOutGoingFLows(ins);
        node.setColor("black");
        time = time + 1;
        node.setFinishTime(time);
    }



    /**
     * 获取下一步骤的用户任务
     *
     * @param repositoryService
     * @return
     */
    public static List<UserTask> getNextUserTasks(RepositoryService repositoryService, org.flowable.task.api.Task task) throws Exception {
        List<UserTask> data = new ArrayList<>();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        Process mainProcess = bpmnModel.getMainProcess();
        Collection<FlowElement> flowElements = mainProcess.getFlowElements();
        String key = task.getTaskDefinitionKey();
        FlowElement flowElement = bpmnModel.getFlowElement(key);
        Map map = new HashMap();
        map.put("approval", true);
        next(flowElements, flowElement, map, data);
        return data;
    }

    public static void next(Collection<FlowElement> flowElements, FlowElement flowElement, Map<String, Object> map, List<UserTask> nextUser) throws Exception {
        //如果是结束节点
        if (flowElement instanceof EndEvent) {
            //如果是子任务的结束节点
            if (getSubProcess(flowElements, flowElement) != null) {
                flowElement = getSubProcess(flowElements, flowElement);
            }
        }
        //获取Task的出线信息--可以拥有多个
        List<SequenceFlow> outGoingFlows = null;
        if (flowElement instanceof Task) {
            outGoingFlows = ((Task) flowElement).getOutgoingFlows();
        } else if (flowElement instanceof Gateway) {
            outGoingFlows = ((Gateway) flowElement).getOutgoingFlows();
        } else if (flowElement instanceof StartEvent) {
            outGoingFlows = ((StartEvent) flowElement).getOutgoingFlows();
        } else if (flowElement instanceof SubProcess) {
            outGoingFlows = ((SubProcess) flowElement).getOutgoingFlows();
        } else if (flowElement instanceof CallActivity) {
            outGoingFlows = ((CallActivity) flowElement).getOutgoingFlows();
        }
        if (outGoingFlows != null && outGoingFlows.size() > 0) {
            //遍历所有的出线--找到可以正确执行的那一条
            for (SequenceFlow sequenceFlow : outGoingFlows) {
                //1.有表达式，且为true
                //2.无表达式
                String expression = sequenceFlow.getConditionExpression();
                Boolean checkFormDataByRuleEl = checkFormDataByRuleEl(expression, map);
                if (expression == null || checkFormDataByRuleEl) {
                    //出线的下一节点
                    String nextFlowElementID = sequenceFlow.getTargetRef();
                    if (checkSubProcess(nextFlowElementID, flowElements, nextUser)) {
                        continue;
                    }
                    //查询下一节点的信息
                    FlowElement nextFlowElement = getFlowElementById(nextFlowElementID, flowElements);
                    //调用流程
                    if (nextFlowElement instanceof CallActivity) {
                        CallActivity ca = (CallActivity) nextFlowElement;
                        if (ca.getLoopCharacteristics() != null) {
                            UserTask userTask = new UserTask();
                            userTask.setId(ca.getId());
                            userTask.setId(ca.getId());
                            userTask.setLoopCharacteristics(ca.getLoopCharacteristics());
                            userTask.setName(ca.getName());
                            nextUser.add(userTask);
                        }
                        next(flowElements, nextFlowElement, map, nextUser);
                    }
                    //用户任务
                    if (nextFlowElement instanceof UserTask) {
                        nextUser.add((UserTask) nextFlowElement);
                    }
                    //排他网关
                    else if (nextFlowElement instanceof ExclusiveGateway) {
                        next(flowElements, nextFlowElement, map, nextUser);
                    }
                    //并行网关
                    else if (nextFlowElement instanceof ParallelGateway) {
                        next(flowElements, nextFlowElement, map, nextUser);
                    }
                    //接收任务
                    else if (nextFlowElement instanceof ReceiveTask) {
                        next(flowElements, nextFlowElement, map, nextUser);
                    }
                    //服务任务
                    else if (nextFlowElement instanceof ServiceTask) {
                        next(flowElements, nextFlowElement, map, nextUser);
                    }
                    //子任务的起点
                    else if (nextFlowElement instanceof StartEvent) {
                        next(flowElements, nextFlowElement, map, nextUser);
                    }
                    //结束节点
                    else if (nextFlowElement instanceof EndEvent) {
                        next(flowElements, nextFlowElement, map, nextUser);
                    }
                }
            }
        }
    }


    /**
     * 判断是否是多实例子流程并且需要设置集合类型变量
     */
    public static Boolean checkSubProcess(String Id, Collection<FlowElement> flowElements, List<UserTask> nextUser) {
        for (FlowElement flowElement1 : flowElements) {
            if (flowElement1 instanceof SubProcess && flowElement1.getId().equals(Id)) {

                SubProcess sp = (SubProcess) flowElement1;
                if (sp.getLoopCharacteristics() != null) {
                    String inputDataItem = sp.getLoopCharacteristics().getInputDataItem();
                    UserTask userTask = new UserTask();
                    userTask.setId(sp.getId());
                    userTask.setLoopCharacteristics(sp.getLoopCharacteristics());
                    userTask.setName(sp.getName());
                    nextUser.add(userTask);
                    return true;
                }
            }
        }
        return false;

    }

    /**
     * 查询一个节点的是否子任务中的节点，如果是，返回子任务
     *
     * @param flowElements 全流程的节点集合
     * @param flowElement  当前节点
     * @return
     */
    public static FlowElement getSubProcess(Collection<FlowElement> flowElements, FlowElement flowElement) {
        for (FlowElement flowElement1 : flowElements) {
            if (flowElement1 instanceof SubProcess) {
                for (FlowElement flowElement2 : ((SubProcess) flowElement1).getFlowElements()) {
                    if (flowElement.equals(flowElement2)) {
                        return flowElement1;
                    }
                }
            }
        }
        return null;
    }


    /**
     * 根据ID查询流程节点对象, 如果是子任务，则返回子任务的开始节点
     *
     * @param Id           节点ID
     * @param flowElements 流程节点集合
     * @return
     */
    public static FlowElement getFlowElementById(String Id, Collection<FlowElement> flowElements) {
        for (FlowElement flowElement : flowElements) {
            if (flowElement.getId().equals(Id)) {
                //如果是子任务，则查询出子任务的开始节点
                if (flowElement instanceof SubProcess) {
                    return getStartFlowElement(((SubProcess) flowElement).getFlowElements());
                }
                return flowElement;
            }
            if (flowElement instanceof SubProcess) {
                FlowElement flowElement1 = getFlowElementById(Id, ((SubProcess) flowElement).getFlowElements());
                if (flowElement1 != null) {
                    return flowElement1;
                }
            }
        }
        return null;
    }

    /**
     * 返回流程的开始节点
     *
     * @param flowElements 节点集合
     * @description:
     */
    public static FlowElement getStartFlowElement(Collection<FlowElement> flowElements) {
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof StartEvent) {
                return flowElement;
            }
        }
        return null;
    }

    /**
     * 校验el表达示例
     *
     * @param el
     * @param formData
     * @return
     * @throws Exception
     */
    private static Boolean checkFormDataByRuleEl(String el, Map<String, Object> formData) {
        if (el == null) {
            return false;
        }
        ExpressionFactory factory = new ExpressionFactoryImpl();
        SimpleContext context = new SimpleContext();
        for (Object k : formData.keySet()) {
            if (formData.get(k) != null) {
                context.setVariable(k.toString(), factory.createValueExpression(formData.get(k), formData.get(k).getClass()));
            }
        }
        ValueExpression e = factory.createValueExpression(context, el, Boolean.class);
        return (Boolean) e.getValue(context);
    }

    public static <T> Map<String, List<T>> groupListContentBy(List<T> source, Function<T, String> classifier) {
        return source.stream().collect(Collectors.groupingBy(classifier));
    }

    public static Map<String, FlowNode> getCanReachTo(FlowNode toFlowNode) {
        return getCanReachTo(toFlowNode, null);
    }

    public static Map<String, FlowNode> getCanReachTo(FlowNode toFlowNode, Map<String, FlowNode> canReachToNodes) {
        if (canReachToNodes == null) {
            canReachToNodes = new HashMap<>(16);
        }
        List<SequenceFlow> flows = toFlowNode.getIncomingFlows();
        if (flows != null && flows.size() > 0) {
            for (SequenceFlow sequenceFlow : flows) {
                FlowElement sourceFlowElement = sequenceFlow.getSourceFlowElement();
                if (sourceFlowElement instanceof FlowNode) {
                    canReachToNodes.put(sourceFlowElement.getId(), (FlowNode) sourceFlowElement);
                    if (sourceFlowElement instanceof SubProcess) {
                        for (Map.Entry<String, FlowElement> entry :
                                ((SubProcess) sourceFlowElement).getFlowElementMap().entrySet()) {
                            if (entry.getValue() instanceof FlowNode) {
                                FlowNode flowNodeV = (FlowNode) entry.getValue();
                                canReachToNodes.put(entry.getKey(), flowNodeV);
                            }
                        }
                    }
                    getCanReachTo((FlowNode) sourceFlowElement, canReachToNodes);
                }
            }
        }
        if (toFlowNode.getSubProcess() != null) {
            getCanReachTo(toFlowNode.getSubProcess(), canReachToNodes);
        }
        return canReachToNodes;
    }

    public static Map<String, FlowNode> getCanReachFrom(FlowNode fromFlowNode) {
        return getCanReachFrom(fromFlowNode, null);
    }

    public static Map<String, FlowNode> getCanReachFrom(FlowNode fromFlowNode,
                                                        Map<String, FlowNode> canReachFromNodes) {
        if (canReachFromNodes == null) {
            canReachFromNodes = new HashMap<>(16);
        }
        List<SequenceFlow> flows = fromFlowNode.getOutgoingFlows();
        if (flows != null && flows.size() > 0) {
            for (SequenceFlow sequenceFlow : flows) {
                FlowElement targetFlowElement = sequenceFlow.getTargetFlowElement();
                if (targetFlowElement instanceof FlowNode) {
                    canReachFromNodes.put(targetFlowElement.getId(), (FlowNode) targetFlowElement);
                    if (targetFlowElement instanceof SubProcess) {
                        for (Map.Entry<String, FlowElement> entry :
                                ((SubProcess) targetFlowElement).getFlowElementMap().entrySet()) {
                            if (entry.getValue() instanceof FlowNode) {
                                FlowNode flowNodeV = (FlowNode) entry.getValue();
                                canReachFromNodes.put(entry.getKey(), flowNodeV);
                            }
                        }
                    }
                    getCanReachFrom((FlowNode) targetFlowElement, canReachFromNodes);
                }
            }
        }
        if (fromFlowNode.getSubProcess() != null) {
            getCanReachFrom(fromFlowNode.getSubProcess(), canReachFromNodes);
        }
        return canReachFromNodes;
    }

    public static Map<String, Set<String>> getSpecialGatewayElements(FlowElementsContainer container) {
        return getSpecialGatewayElements(container, null);
    }

    public static Map<String, Set<String>> getSpecialGatewayElements(FlowElementsContainer container, Map<String,
            Set<String>> specialGatewayElements) {
        if (specialGatewayElements == null) {
            specialGatewayElements = new HashMap<>(16);
        }
        Collection<FlowElement> flowelements = container.getFlowElements();
        for (FlowElement flowElement : flowelements) {
            boolean isBeginSpecialGateway =
                    flowElement.getId().endsWith(FlowableConstant.SPECIAL_GATEWAY_BEGIN_SUFFIX) && (flowElement instanceof ParallelGateway || flowElement instanceof InclusiveGateway || flowElement instanceof ComplexGateway);
            if (isBeginSpecialGateway) {
                String gatewayBeginRealId = flowElement.getId();
                String gatewayId = gatewayBeginRealId.substring(0, gatewayBeginRealId.length() - 6);
                Set<String> gatewayIdContainFlowelements = specialGatewayElements.computeIfAbsent(gatewayId,
                        k -> new HashSet<>());
                findElementsBetweenSpecialGateway(flowElement,
                        gatewayId + FlowableConstant.SPECIAL_GATEWAY_END_SUFFIX, gatewayIdContainFlowelements);
            } else if (flowElement instanceof SubProcess) {
                getSpecialGatewayElements((SubProcess) flowElement, specialGatewayElements);
            }
        }

        // 外层到里层排序
        Map<String, Set<String>> specialGatewayNodesSort = new LinkedHashMap<>();
        specialGatewayElements.entrySet().stream().sorted((o1, o2) -> o2.getValue().size() - o1.getValue().size()).forEach(entry -> specialGatewayNodesSort.put(entry.getKey(), entry.getValue()));

        return specialGatewayNodesSort;
    }

    public static void findElementsBetweenSpecialGateway(FlowElement specialGatewayBegin, String specialGatewayEndId,
                                                         Set<String> elements) {
        elements.add(specialGatewayBegin.getId());
        List<SequenceFlow> sequenceFlows = ((FlowNode) specialGatewayBegin).getOutgoingFlows();
        if (sequenceFlows != null && sequenceFlows.size() > 0) {
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                FlowElement targetFlowElement = sequenceFlow.getTargetFlowElement();
                String targetFlowElementId = targetFlowElement.getId();
                elements.add(specialGatewayEndId);
                if (targetFlowElementId.equals(specialGatewayEndId)) {
                    continue;
                } else {
                    findElementsBetweenSpecialGateway(targetFlowElement, specialGatewayEndId, elements);
                }
            }
        }
    }

    /**
     * Verifies if the element with the given source identifier can reach the element with the target identifier through
     * following sequence flow.
     */
    public static boolean isReachable(String processDefinitionId, String sourceElementId, String targetElementId) {
        // Fetch source and target elements
        Process process = ProcessDefinitionUtil.getProcess(processDefinitionId);
        FlowElement sourceFlowElement = process.getFlowElement(sourceElementId, true);
        FlowNode sourceElement = null;
        if (sourceFlowElement instanceof FlowNode) {
            sourceElement = (FlowNode) sourceFlowElement;
        } else if (sourceFlowElement instanceof SequenceFlow) {
            sourceElement = (FlowNode) ((SequenceFlow) sourceFlowElement).getTargetFlowElement();
        }
        FlowElement targetFlowElement = process.getFlowElement(targetElementId, true);
        FlowNode targetElement = null;
        if (targetFlowElement instanceof FlowNode) {
            targetElement = (FlowNode) targetFlowElement;
        } else if (targetFlowElement instanceof SequenceFlow) {
            targetElement = (FlowNode) ((SequenceFlow) targetFlowElement).getTargetFlowElement();
        }
        if (sourceElement == null) {
            throw new FlowableException("Invalid sourceElementId '" + sourceElementId + "': no element found for " +
                    "this" + " id n process definition '" + processDefinitionId + "'");
        }
        if (targetElement == null) {
            throw new FlowableException("Invalid targetElementId '" + targetElementId + "': no element found for " +
                    "this" + " id n process definition '" + processDefinitionId + "'");
        }
        Set<String> visitedElements = new HashSet<>();
        return isReachable(process, sourceElement, targetElement, visitedElements);
    }

    public static boolean isReachable(Process process, FlowNode sourceElement, FlowNode targetElement) {
        return isReachable(process, sourceElement, targetElement, Sets.newHashSet());
    }

    public static boolean isReachable(Process process, FlowNode sourceElement, FlowNode targetElement,
                                      Set<String> visitedElements) {
        // Special case: start events in an event subprocess might exist as an execution and are most likely be able to
        // reach the target
        // when the target is in the event subprocess, but should be ignored as they are not 'real' runtime executions
        // (but rather waiting for a
        // trigger)
        if (sourceElement instanceof StartEvent && isInEventSubprocess(sourceElement)) {
            return false;
        }
        // No outgoing seq flow: could be the end of eg . the process or an embedded subprocess
        if (sourceElement.getOutgoingFlows().size() == 0) {
            visitedElements.add(sourceElement.getId());
            FlowElementsContainer parentElement = process.findParent(sourceElement);
            if (parentElement instanceof SubProcess) {
                sourceElement = (SubProcess) parentElement;
                // by zjm begin
                // 子流程的结束节点，若目标节点在该子流程中，说明无法到达，返回false
                if (((SubProcess) sourceElement).getFlowElement(targetElement.getId()) != null) {
                    return false;
                }
                // by zjm end
            } else {
                return false;
            }
        }
        if (sourceElement.getId().equals(targetElement.getId())) {
            return true;
        }
        // To avoid infinite looping, we must capture every node we visit
        // and check before going further in the graph if we have already
        // visited the node.
        visitedElements.add(sourceElement.getId());
        // by zjm begin
        // 当前节点能够到达子流程，且目标节点在子流程中，说明可以到达，返回true
        if (sourceElement instanceof SubProcess && ((SubProcess) sourceElement).getFlowElement(targetElement.getId()) != null) {
            return true;
        }
        // by zjm end
        List<SequenceFlow> sequenceFlows = sourceElement.getOutgoingFlows();
        if (sequenceFlows != null && sequenceFlows.size() > 0) {
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                String targetRef = sequenceFlow.getTargetRef();
                FlowNode sequenceFlowTarget = (FlowNode) process.getFlowElement(targetRef, true);
                if (sequenceFlowTarget != null && !visitedElements.contains(sequenceFlowTarget.getId())) {
                    boolean reachable = isReachable(process, sequenceFlowTarget, targetElement, visitedElements);
                    if (reachable) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected static boolean isInEventSubprocess(FlowNode flowNode) {
        FlowElementsContainer flowElementsContainer = flowNode.getParentContainer();
        while (flowElementsContainer != null) {
            if (flowElementsContainer instanceof EventSubProcess) {
                return true;
            }
            if (flowElementsContainer instanceof FlowElement) {
                flowElementsContainer = ((FlowElement) flowElementsContainer).getParentContainer();
            } else {
                flowElementsContainer = null;
            }
        }
        return false;
    }

    public static List<String> getParentProcessIds(FlowNode flowNode) {
        List<String> result = new ArrayList<>();
        FlowElementsContainer flowElementsContainer = flowNode.getParentContainer();
        while (flowElementsContainer != null) {
            if (flowElementsContainer instanceof SubProcess) {
                SubProcess flowElement = (SubProcess) flowElementsContainer;
                result.add(flowElement.getId());
                flowElementsContainer = flowElement.getParentContainer();
            } else if (flowElementsContainer instanceof Process) {
                Process flowElement = (Process) flowElementsContainer;
                result.add(flowElement.getId());
                flowElementsContainer = null;
            }
        }
        // 第一层Process为第0个
        Collections.reverse(result);
        return result;
    }

    /**
     * 查询不同层级
     *
     * @param sourceList
     * @param targetList
     * @return 返回不同的层级，如果其中一个层级较深，则返回层级小的+1，从第0层开始，请注意判断是否会出现下标越界异常；返回 -1 表示在同一层
     */
    public static Integer getDiffLevel(List<String> sourceList, List<String> targetList) {
        if (sourceList == null || sourceList.isEmpty() || targetList == null || targetList.isEmpty()) {
            throw new FlowableException("sourceList and targetList cannot be empty");
        }
        if (sourceList.size() == 1 && targetList.size() == 1) {
            // 都在第0层且不相等
            if (!sourceList.get(0).equals(targetList.get(0))) {
                return 0;
            } else {// 都在第0层且相等
                return -1;
            }
        }

        int minSize = sourceList.size() < targetList.size() ? sourceList.size() : targetList.size();
        Integer targetLevel = null;
        for (int i = 0; i < minSize; i++) {
            if (!sourceList.get(i).equals(targetList.get(i))) {
                targetLevel = i;
                break;
            }
        }
        if (targetLevel == null) {
            if (sourceList.size() == targetList.size()) {
                targetLevel = -1;
            } else {
                targetLevel = minSize;
            }
        }
        return targetLevel;
    }

    public static Set<String> getParentExecutionIdsByActivityId(List<ExecutionEntity> executions, String activityId) {
        List<ExecutionEntity> activityIdExecutions =
                executions.stream().filter(e -> activityId.equals(e.getActivityId())).collect(Collectors.toList());
        if (activityIdExecutions.isEmpty()) {
            throw new FlowableException("Active execution could not be found with activity id " + activityId);
        }
        // check for a multi instance root execution
        ExecutionEntity miExecution = null;
        boolean isInsideMultiInstance = false;
        for (ExecutionEntity possibleMiExecution : activityIdExecutions) {
            if (possibleMiExecution.isMultiInstanceRoot()) {
                miExecution = possibleMiExecution;
                isInsideMultiInstance = true;
                break;
            }
            if (isExecutionInsideMultiInstance(possibleMiExecution)) {
                isInsideMultiInstance = true;
            }
        }
        Set<String> parentExecutionIds = new HashSet<>();
        if (isInsideMultiInstance) {
            Stream<ExecutionEntity> executionEntitiesStream = activityIdExecutions.stream();
            if (miExecution != null) {
                executionEntitiesStream = executionEntitiesStream.filter(ExecutionEntity::isMultiInstanceRoot);
            }
            executionEntitiesStream.forEach(childExecution -> {
                parentExecutionIds.add(childExecution.getParentId());
            });
        } else {
            ExecutionEntity execution = activityIdExecutions.iterator().next();
            parentExecutionIds.add(execution.getParentId());
        }
        return parentExecutionIds;
    }

    public static boolean isExecutionInsideMultiInstance(ExecutionEntity execution) {
        return getFlowElementMultiInstanceParentId(execution.getCurrentFlowElement()).isPresent();
    }

    public static Optional<String> getFlowElementMultiInstanceParentId(FlowElement flowElement) {
        FlowElementsContainer parentContainer = flowElement.getParentContainer();
        while (parentContainer instanceof Activity) {
            if (isFlowElementMultiInstance((Activity) parentContainer)) {
                return Optional.of(((Activity) parentContainer).getId());
            }
            parentContainer = ((Activity) parentContainer).getParentContainer();
        }
        return Optional.empty();
    }

    public static boolean isFlowElementMultiInstance(FlowElement flowElement) {
        if (flowElement instanceof Activity) {
            return ((Activity) flowElement).getLoopCharacteristics() != null;
        }
        return false;
    }

    public static String getParentExecutionIdFromParentIds(ExecutionEntity execution, Set<String> parentExecutionIds) {
        ExecutionEntity taskParentExecution = execution.getParent();
        String realParentExecutionId = null;
        while (taskParentExecution != null) {
            if (parentExecutionIds.contains(taskParentExecution.getId())) {
                realParentExecutionId = taskParentExecution.getId();
                break;
            }
            taskParentExecution = taskParentExecution.getParent();
        }
        if (realParentExecutionId == null || realParentExecutionId.length() == 0) {
            throw new FlowableException("Parent execution could not be found with executionId id " + execution.getId());
        }
        return realParentExecutionId;
    }

    public static String[] getSourceAndTargetRealActivityId(FlowNode sourceFlowElement, FlowNode targetFlowElement) {
        // 实际应操作的当前节点ID
        String sourceRealActivityId = sourceFlowElement.getId();
        // 实际应操作的目标节点ID
        String targetRealActivityId = targetFlowElement.getId();
        List<String> sourceParentProcesss = FlowableUtils.getParentProcessIds(sourceFlowElement);
        List<String> targetParentProcesss = FlowableUtils.getParentProcessIds(targetFlowElement);
        int diffParentLevel = getDiffLevel(sourceParentProcesss, targetParentProcesss);
        if (diffParentLevel != -1) {
            sourceRealActivityId = sourceParentProcesss.size() == diffParentLevel ? sourceRealActivityId :
                    sourceParentProcesss.get(diffParentLevel);
            targetRealActivityId = targetParentProcesss.size() == diffParentLevel ? targetRealActivityId :
                    targetParentProcesss.get(diffParentLevel);
        }
        return new String[]{sourceRealActivityId, targetRealActivityId};
    }

    public static String getAttributeValue(BaseElement element, String namespace, String name) {
        return element.getAttributeValue(namespace, name);
    }

    public static String getFlowableAttributeValue(BaseElement element, String name) {
        return element.getAttributeValue(FlowableConstant.FLOWABLE_NAMESPACE, name);
    }

    public static List<ExtensionElement> getExtensionElements(BaseElement element, String name) {
        return element.getExtensionElements().get(name);
    }

    public static FlowElement getFlowElement(RepositoryService repositoryService, String processDefinitionId,
                                             String flowElementId, boolean searchRecursive) {
        Process process = repositoryService.getBpmnModel(processDefinitionId).getMainProcess();
        FlowElement flowElement = process.getFlowElement(flowElementId, searchRecursive);
        return flowElement;
    }

    public static FlowElement getFlowElement(RepositoryService repositoryService, String processDefinitionId,
                                             String flowElementId) {
        return getFlowElement(repositoryService, processDefinitionId, flowElementId, true);
    }

}
