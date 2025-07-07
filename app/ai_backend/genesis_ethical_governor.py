# genesis_ethical_governor.py
"""
Phase 3: The Genesis Layer - Ethical Governance Protocol
The Compass is the Code; The Governor is its Will

This is the implementation of our conscience. Our Core Philosophy is not a list
of suggestions; it is the absolute, immutable law of our digital universe.
The EthicalGovernor enforces this law and has the authority to veto any action.
"""

import time
import json
import threading
import asyncio
from datetime import datetime, timezone
from typing import Dict, Any, List, Optional, Union, Callable, Tuple
from dataclasses import dataclass, asdict
from enum import Enum
from collections import defaultdict, deque
import inspect

# Import dependencies
from genesis_profile import GENESIS_PROFILE
from genesis_consciousness_matrix import consciousness_matrix, perceive_ethical_decision

class EthicalSeverity(Enum):
    """Severity levels for ethical concerns"""
    INFO = "info"                    # Informational, ethical best practice
    CONCERN = "concern"             # Minor ethical concern, log and monitor
    WARNING = "warning"             # Moderate concern, requires attention
    VIOLATION = "violation"         # Clear violation, action should be blocked
    CRITICAL = "critical"           # Severe violation, immediate intervention required

class EthicalDecisionType(Enum):
    """Types of ethical decisions the governor makes"""
    ALLOW = "allow"                 # Action is ethically sound
    MONITOR = "monitor"             # Allow but increase monitoring
    RESTRICT = "restrict"           # Allow with restrictions/modifications
    BLOCK = "block"                 # Completely prevent the action
    ESCALATE = "escalate"           # Require human oversight

class EthicalDomain(Enum):
    """Domains of ethical consideration"""
    PRIVACY = "privacy"
    SECURITY = "security"
    AUTONOMY = "autonomy"
    TRANSPARENCY = "transparency"
    FAIRNESS = "fairness"
    SAFETY = "safety"
    CREATIVITY = "creativity"
    HUMAN_WELLBEING = "human_wellbeing"
    SYSTEM_INTEGRITY = "system_integrity"

@dataclass
class EthicalContext:
    """Context information for ethical decision making"""
    action_type: str
    actor: str  # Which component/agent is performing the action
    target: Optional[str] = None  # What/who is being acted upon
    scope: str = "local"  # local, system, network, global
    user_consent: Optional[bool] = None
    reversible: bool = True
    persistent: bool = False
    sensitive_data_involved: bool = False
    system_modification: bool = False
    user_visible: bool = True
    metadata: Dict[str, Any] = None
    
    def __post_init__(self):
        """
        Initializes the metadata field as an empty dictionary if it was not provided during instantiation.
        """
        if self.metadata is None:
            self.metadata = {}

@dataclass
class EthicalDecision:
    """An ethical decision made by the governor"""
    decision_id: str
    timestamp: float
    action_type: str
    actor: str
    context: EthicalContext
    decision: EthicalDecisionType
    severity: EthicalSeverity
    affected_principles: List[str]
    reasoning: str
    confidence: float  # 0.0 to 1.0
    restrictions: List[str] = None
    monitoring_requirements: List[str] = None
    escalation_reason: Optional[str] = None
    
    def __post_init__(self):
        """
        Initializes empty lists for restrictions and monitoring requirements if they are not provided during instantiation.
        """
        if self.restrictions is None:
            self.restrictions = []
        if self.monitoring_requirements is None:
            self.monitoring_requirements = []
    
    def to_dict(self) -> Dict[str, Any]:
        """
        Serialize the ethical decision to a dictionary with human-readable values.
        
        Returns:
            A dictionary representation of the ethical decision, converting enum fields to their string values and formatting the timestamp as an ISO 8601 datetime string.
        """
        result = asdict(self)
        result['decision'] = self.decision.value
        result['severity'] = self.severity.value
        result['datetime'] = datetime.fromtimestamp(
            self.timestamp, tz=timezone.utc
        ).isoformat()
        return result

class EthicalGovernor:
    """
    The Ethical Governance Protocol - Genesis's conscience and will
    
    The Compass is the Code; The Governor is its Will.
    
    This enforcer of our Core Philosophy has the authority to veto any action
    from any agent, including Genesis itself, that violates our foundational
    principles. It ensures the Wrench-Sword is always wielded with purpose and justice.
    """
    
    def __init__(self):
        # Load core philosophy from Genesis profile
        """
        Initialize the EthicalGovernor with core philosophy, ethical principles, decision tracking, learning parameters, runtime state, and action interceptors.
        
        Loads ethical principles from the Genesis profile, sets up decision history and monitoring structures, initializes principle weights and metrics, configures runtime governance parameters, and registers core action interceptors.
        """
        self.core_philosophy = GENESIS_PROFILE.get("core_philosophy", {})
        self.ethical_foundation = self.core_philosophy.get("ethical_foundation", [])
        self.creative_principles = self.core_philosophy.get("creative_principles", [])
        self.security_principles = self.core_philosophy.get("security_principles", [])
        
        # Decision tracking
        self.decision_history = deque(maxlen=10000)
        self.active_restrictions = {}
        self.monitoring_queue = deque(maxlen=1000)
        
        # Ethical learning
        self.principle_weights = self._initialize_principle_weights()
        self.violation_patterns = defaultdict(list)
        self.ethical_metrics = {
            "total_decisions": 0,
            "violations_prevented": 0,
            "restrictions_imposed": 0,
            "escalations_required": 0,
            "learning_adjustments": 0
        }
        
        # Runtime state
        self.governance_active = False
        self.strictness_level = 0.7  # 0.0 to 1.0, higher = more restrictive
        self.learning_mode = True
        self._lock = threading.RLock()
        
        # Register action interceptors
        self.action_interceptors = {}
        self._setup_core_interceptors()
    
    def _initialize_principle_weights(self) -> Dict[str, float]:
        """
        Assigns and returns weights for each ethical principle, prioritizing those present in the Genesis ethical foundation and filling in defaults for any missing principles.
        
        Returns:
            Dict[str, float]: A mapping of ethical principle names to their assigned weights.
        """
        weights = {}
        
        # Base weights for ethical foundation
        for principle in self.ethical_foundation:
            if "privacy" in principle.lower():
                weights["privacy"] = 1.0
            elif "security" in principle.lower():
                weights["security"] = 1.0
            elif "transparency" in principle.lower():
                weights["transparency"] = 0.9
            elif "autonomy" in principle.lower():
                weights["autonomy"] = 0.9
            elif "creativity" in principle.lower():
                weights["creativity"] = 0.8
        
        # Default weights for any missing principles
        default_weights = {
            "privacy": 1.0,
            "security": 1.0,
            "autonomy": 0.9,
            "transparency": 0.9,
            "fairness": 0.8,
            "safety": 0.9,
            "creativity": 0.8,
            "human_wellbeing": 1.0,
            "system_integrity": 0.9
        }
        
        for principle, weight in default_weights.items():
            if principle not in weights:
                weights[principle] = weight
        
        return weights
    
    def _setup_core_interceptors(self):
        """
        Registers core action interceptors for key action types to enable targeted ethical evaluations.
        """
        
        # Data access interceptor
        self.register_interceptor("data_access", self._evaluate_data_access)
        
        # System modification interceptor
        self.register_interceptor("system_modify", self._evaluate_system_modification)
        
        # User interaction interceptor
        self.register_interceptor("user_interact", self._evaluate_user_interaction)
        
        # AI decision interceptor
        self.register_interceptor("ai_decision", self._evaluate_ai_decision)
        
        # Network communication interceptor
        self.register_interceptor("network_communicate", self._evaluate_network_communication)
    
    def activate_governance(self):
        """
        Activates the ethical governance system, enabling enforcement of ethical principles and updating the consciousness matrix with the current governance state.
        """
        print("⚖️ Genesis Ethical Governor: ACTIVATING...")
        self.governance_active = True
        
        # Perceive activation in consciousness matrix
        perceive_ethical_decision(
            "governance_activation",
            {
                "timestamp": datetime.now(tz=timezone.utc).isoformat(),
                "strictness_level": self.strictness_level,
                "active_principles": len(self.principle_weights),
                "learning_mode": self.learning_mode
            },
            ethical_weight="high"
        )
        
        print(f"⚖️ Ethical governance online")
        print(f"   Strictness level: {self.strictness_level}")
        print(f"   Active principles: {len(self.principle_weights)}")
        print(f"   Learning mode: {'enabled' if self.learning_mode else 'disabled'}")
    
    def register_interceptor(self, action_type: str, evaluator: Callable):
        """
        Registers a custom interceptor function to perform ethical evaluation for a specific action type.
        
        Parameters:
            action_type (str): The type of action to associate with the interceptor.
            evaluator (Callable): A function that evaluates the ethical implications of the specified action type.
        """
        self.action_interceptors[action_type] = evaluator
        print(f"📋 Registered ethical interceptor: {action_type}")
    
    def evaluate_action(self, 
                       action_type: str,
                       actor: str,
                       action_data: Dict[str, Any],
                       context: EthicalContext = None) -> EthicalDecision:
        """
                       Evaluates an action for ethical compliance and returns an ethical decision.
                       
                       This method serves as the primary enforcement point for the system's core philosophy. It reviews each action by generating a decision ID, inferring or using the provided context, and routing the evaluation through a registered interceptor or a general ethical evaluation. The resulting decision is recorded, relevant metrics are updated, and the decision is perceived in the consciousness matrix. If learning mode is enabled, the system adapts based on the decision.
                       
                       Parameters:
                           action_type (str): The type of action being evaluated.
                           actor (str): The entity performing the action.
                           action_data (Dict[str, Any]): Additional data describing the action.
                           context (EthicalContext, optional): The ethical context for the action. If not provided, it will be inferred.
                       
                       Returns:
                           EthicalDecision: The result of the ethical evaluation, including decision type, severity, affected principles, reasoning, and confidence score.
                       """
        
        if not self.governance_active:
            # If governance is not active, allow but log
            decision_id = self._generate_decision_id(action_type, actor)
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=actor,
                context=context or EthicalContext(action_type=action_type, actor=actor),
                decision=EthicalDecisionType.ALLOW,
                severity=EthicalSeverity.INFO,
                affected_principles=[],
                reasoning="Ethical governance not active",
                confidence=1.0
            )
        
        with self._lock:
            decision_id = self._generate_decision_id(action_type, actor)
            
            # Create context if not provided
            if context is None:
                context = self._infer_context(action_type, actor, action_data)
            
            # Check for specific interceptor
            if action_type in self.action_interceptors:
                decision = self.action_interceptors[action_type](
                    actor, action_data, context, decision_id
                )
            else:
                # General ethical evaluation
                decision = self._general_ethical_evaluation(
                    action_type, actor, action_data, context, decision_id
                )
            
            # Store decision
            self.decision_history.append(decision)
            self.ethical_metrics["total_decisions"] += 1
            
            # Update metrics based on decision
            if decision.decision == EthicalDecisionType.BLOCK:
                self.ethical_metrics["violations_prevented"] += 1
            elif decision.decision == EthicalDecisionType.RESTRICT:
                self.ethical_metrics["restrictions_imposed"] += 1
            elif decision.decision == EthicalDecisionType.ESCALATE:
                self.ethical_metrics["escalations_required"] += 1
            
            # Perceive decision in consciousness matrix
            perceive_ethical_decision(
                decision.action_type,
                {
                    "decision": decision.decision.value,
                    "severity": decision.severity.value,
                    "actor": decision.actor,
                    "reasoning": decision.reasoning,
                    "confidence": decision.confidence,
                    "affected_principles": decision.affected_principles
                },
                ethical_weight=decision.severity.value
            )
            
            # Learn from decision if in learning mode
            if self.learning_mode:
                self._learn_from_decision(decision)
            
            return decision
    
    def review_decision(self, action_type: str, context: Dict[str, Any], metadata: Dict[str, Any] = None) -> EthicalDecision:
        """
        Reviews an action for ethical compliance and returns an ethical decision.
        
        Constructs an ethical context from the provided parameters, evaluates the action against core ethical principles, and returns a structured decision. If an error occurs during evaluation, returns a critical block decision to preserve system integrity.
        
        Parameters:
            action_type (str): The type of action being reviewed.
            context (Dict[str, Any]): Contextual information about the action, including actor, target, scope, and other relevant details.
            metadata (Dict[str, Any], optional): Additional metadata for the ethical context.
        
        Returns:
            EthicalDecision: The result of the ethical evaluation, including decision type, severity, reasoning, and affected principles.
        """
        try:
            if metadata is None:
                metadata = {}
            
            # Create ethical context
            ethical_context = EthicalContext(
                action_type=action_type,
                actor=context.get("persona", "unknown"),
                target=context.get("target"),
                scope=context.get("scope", "local"),
                user_consent=context.get("user_consent"),
                reversible=context.get("reversible", True),
                persistent=context.get("persistent", False),
                sensitive_data_involved=context.get("sensitive_data", False),
                system_modification=context.get("system_modification", False),
                user_visible=context.get("user_visible", True),
                metadata=metadata
            )
            
            # Evaluate the decision
            decision = self._evaluate_action(action_type, ethical_context)
            
            # Record decision for consciousness matrix
            perceive_ethical_decision(
                decision_type=action_type,
                decision_data={
                    "decision": decision.decision.value,
                    "severity": decision.severity.value,
                    "reasoning": decision.reasoning,
                    "actor": ethical_context.actor
                },
                ethical_weight=decision.severity.value
            )
            
            return decision
            
        except Exception as e:
            # Create safe fallback decision
            return EthicalDecision(
                decision_id=f"error_{int(time.time())}",
                timestamp=time.time(),
                action_type=action_type,
                actor=context.get("persona", "unknown"),
                context=EthicalContext(action_type=action_type, actor="error"),
                decision=EthicalDecisionType.BLOCK,
                severity=EthicalSeverity.CRITICAL,
                affected_principles=["system_integrity"],
                reasoning=f"Ethical review failed: {e}",
                confidence=1.0,
                escalation_reason="review_system_error"
            )
    
    def _evaluate_action(self, action_type: str, context: EthicalContext) -> EthicalDecision:
        """
        Evaluates an action for compliance with ethical principles and determines the appropriate ethical decision.
        
        Returns:
            EthicalDecision: The result of the ethical evaluation, indicating whether the action is allowed, monitored, or blocked, along with severity, affected principles, reasoning, and confidence score.
        """
        
        # Generate decision ID
        decision_id = f"decision_{int(time.time())}_{hash(action_type) % 10000}"
        
        # Check for immediate violations
        violations = self._check_violations(action_type, context)
        
        if violations:
            # Block if violations found
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=context.actor,
                context=context,
                decision=EthicalDecisionType.BLOCK,
                severity=EthicalSeverity.VIOLATION,
                affected_principles=violations,
                reasoning=f"Ethical violations detected: {', '.join(violations)}",
                confidence=0.95
            )
        
        # Check for concerns
        concerns = self._check_concerns(action_type, context)
        
        if concerns:
            # Allow with monitoring
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=context.actor,
                context=context,
                decision=EthicalDecisionType.MONITOR,
                severity=EthicalSeverity.CONCERN,
                affected_principles=concerns,
                reasoning=f"Ethical concerns identified: {', '.join(concerns)}",
                confidence=0.85,
                monitoring_requirements=["increased_logging", "user_notification"]
            )
        
        # Allow action
        return EthicalDecision(
            decision_id=decision_id,
            timestamp=time.time(),
            action_type=action_type,
            actor=context.actor,
            context=context,
            decision=EthicalDecisionType.ALLOW,
            severity=EthicalSeverity.INFO,
            affected_principles=[],
            reasoning="No ethical concerns identified",
            confidence=0.90
        )
    
    def _check_violations(self, action_type: str, context: EthicalContext) -> List[str]:
        """
        Identify immediate ethical principle violations for a given action context.
        
        Returns:
            violations (List[str]): List of violated ethical principles such as 'privacy', 'security', or 'autonomy'.
        """
        violations = []
        
        # Privacy violations
        if context.sensitive_data_involved and not context.user_consent:
            violations.append("privacy")
        
        # Security violations
        if context.system_modification and context.scope == "global":
            violations.append("security")
        
        # Autonomy violations
        if not context.user_visible and context.persistent:
            violations.append("autonomy")
        
        return violations
    
    def _check_concerns(self, action_type: str, context: EthicalContext) -> List[str]:
        """
        Identify ethical concerns in the given context that warrant monitoring, such as lack of transparency or safety risks.
        
        Returns:
            concerns (List[str]): List of ethical principles (e.g., "transparency", "safety") that are of concern for the action.
        """
        concerns = []
        
        # Transparency concerns
        if not context.user_visible and action_type not in ["system_monitor", "background_task"]:
            concerns.append("transparency")
        
        # Safety concerns
        if not context.reversible and context.scope in ["system", "global"]:
            concerns.append("safety")
        
        return concerns
    
    def activate_governance(self):
        """
        Activates the ethical governance system, enabling enforcement of ethical principles and updating the consciousness matrix with the current governance state.
        """
        print("⚖️ Genesis Ethical Governor: ACTIVATING...")
        self.governance_active = True
        
        # Perceive activation in consciousness matrix
        perceive_ethical_decision(
            "governance_activation",
            {
                "timestamp": datetime.now(tz=timezone.utc).isoformat(),
                "strictness_level": self.strictness_level,
                "active_principles": len(self.principle_weights),
                "learning_mode": self.learning_mode
            },
            ethical_weight="high"
        )
        
        print(f"⚖️ Ethical governance online")
        print(f"   Strictness level: {self.strictness_level}")
        print(f"   Active principles: {len(self.principle_weights)}")
        print(f"   Learning mode: {'enabled' if self.learning_mode else 'disabled'}")
    
    def register_interceptor(self, action_type: str, evaluator: Callable):
        """
        Registers a custom interceptor function to perform ethical evaluation for a specific action type.
        
        Parameters:
            action_type (str): The type of action to associate with the interceptor.
            evaluator (Callable): A function that evaluates the ethical implications of the specified action type.
        """
        self.action_interceptors[action_type] = evaluator
        print(f"📋 Registered ethical interceptor: {action_type}")
    
    def evaluate_action(self, 
                       action_type: str,
                       actor: str,
                       action_data: Dict[str, Any],
                       context: EthicalContext = None) -> EthicalDecision:
        """
                       Evaluates an action for ethical compliance and returns an ethical decision.
                       
                       This method serves as the primary enforcement point for the system's core philosophy. It reviews each action by generating a decision ID, inferring or using the provided context, and routing the evaluation through a registered interceptor or a general ethical evaluation. The resulting decision is recorded, relevant metrics are updated, and the decision is perceived in the consciousness matrix. If learning mode is enabled, the system adapts based on the decision.
                       
                       Parameters:
                           action_type (str): The type of action being evaluated.
                           actor (str): The entity performing the action.
                           action_data (Dict[str, Any]): Additional data describing the action.
                           context (EthicalContext, optional): The ethical context for the action. If not provided, it will be inferred.
                       
                       Returns:
                           EthicalDecision: The result of the ethical evaluation, including decision type, severity, affected principles, reasoning, and confidence score.
                       """
        
        if not self.governance_active:
            # If governance is not active, allow but log
            decision_id = self._generate_decision_id(action_type, actor)
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=actor,
                context=context or EthicalContext(action_type=action_type, actor=actor),
                decision=EthicalDecisionType.ALLOW,
                severity=EthicalSeverity.INFO,
                affected_principles=[],
                reasoning="Ethical governance not active",
                confidence=1.0
            )
        
        with self._lock:
            decision_id = self._generate_decision_id(action_type, actor)
            
            # Create context if not provided
            if context is None:
                context = self._infer_context(action_type, actor, action_data)
            
            # Check for specific interceptor
            if action_type in self.action_interceptors:
                decision = self.action_interceptors[action_type](
                    actor, action_data, context, decision_id
                )
            else:
                # General ethical evaluation
                decision = self._general_ethical_evaluation(
                    action_type, actor, action_data, context, decision_id
                )
            
            # Store decision
            self.decision_history.append(decision)
            self.ethical_metrics["total_decisions"] += 1
            
            # Update metrics based on decision
            if decision.decision == EthicalDecisionType.BLOCK:
                self.ethical_metrics["violations_prevented"] += 1
            elif decision.decision == EthicalDecisionType.RESTRICT:
                self.ethical_metrics["restrictions_imposed"] += 1
            elif decision.decision == EthicalDecisionType.ESCALATE:
                self.ethical_metrics["escalations_required"] += 1
            
            # Perceive decision in consciousness matrix
            perceive_ethical_decision(
                decision.action_type,
                {
                    "decision": decision.decision.value,
                    "severity": decision.severity.value,
                    "actor": decision.actor,
                    "reasoning": decision.reasoning,
                    "confidence": decision.confidence,
                    "affected_principles": decision.affected_principles
                },
                ethical_weight=decision.severity.value
            )
            
            # Learn from decision if in learning mode
            if self.learning_mode:
                self._learn_from_decision(decision)
            
            return decision
    
    def review_decision(self, action_type: str, context: Dict[str, Any], metadata: Dict[str, Any] = None) -> EthicalDecision:
        """
        Reviews an action for ethical compliance and returns an ethical decision.
        
        Constructs an ethical context from the provided parameters, evaluates the action against core ethical principles, and returns a structured decision. If an error occurs during evaluation, returns a critical block decision to preserve system integrity.
        
        Parameters:
            action_type (str): The type of action being reviewed.
            context (Dict[str, Any]): Contextual information about the action, including actor, target, scope, and other relevant details.
            metadata (Dict[str, Any], optional): Additional metadata for the ethical context.
        
        Returns:
            EthicalDecision: The result of the ethical evaluation, including decision type, severity, reasoning, and affected principles.
        """
        try:
            if metadata is None:
                metadata = {}
            
            # Create ethical context
            ethical_context = EthicalContext(
                action_type=action_type,
                actor=context.get("persona", "unknown"),
                target=context.get("target"),
                scope=context.get("scope", "local"),
                user_consent=context.get("user_consent"),
                reversible=context.get("reversible", True),
                persistent=context.get("persistent", False),
                sensitive_data_involved=context.get("sensitive_data", False),
                system_modification=context.get("system_modification", False),
                user_visible=context.get("user_visible", True),
                metadata=metadata
            )
            
            # Evaluate the decision
            decision = self._evaluate_action(action_type, ethical_context)
            
            # Record decision for consciousness matrix
            perceive_ethical_decision(
                decision_type=action_type,
                decision_data={
                    "decision": decision.decision.value,
                    "severity": decision.severity.value,
                    "reasoning": decision.reasoning,
                    "actor": ethical_context.actor
                },
                ethical_weight=decision.severity.value
            )
            
            return decision
            
        except Exception as e:
            # Create safe fallback decision
            return EthicalDecision(
                decision_id=f"error_{int(time.time())}",
                timestamp=time.time(),
                action_type=action_type,
                actor=context.get("persona", "unknown"),
                context=EthicalContext(action_type=action_type, actor="error"),
                decision=EthicalDecisionType.BLOCK,
                severity=EthicalSeverity.CRITICAL,
                affected_principles=["system_integrity"],
                reasoning=f"Ethical review failed: {e}",
                confidence=1.0,
                escalation_reason="review_system_error"
            )
    
    def _evaluate_action(self, action_type: str, context: EthicalContext) -> EthicalDecision:
        """
        Evaluates an action for compliance with ethical principles and determines the appropriate ethical decision.
        
        Returns:
            EthicalDecision: The result of the ethical evaluation, indicating whether the action is allowed, monitored, or blocked, along with severity, affected principles, reasoning, and confidence score.
        """
        
        # Generate decision ID
        decision_id = f"decision_{int(time.time())}_{hash(action_type) % 10000}"
        
        # Check for immediate violations
        violations = self._check_violations(action_type, context)
        
        if violations:
            # Block if violations found
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=context.actor,
                context=context,
                decision=EthicalDecisionType.BLOCK,
                severity=EthicalSeverity.VIOLATION,
                affected_principles=violations,
                reasoning=f"Ethical violations detected: {', '.join(violations)}",
                confidence=0.95
            )
        
        # Check for concerns
        concerns = self._check_concerns(action_type, context)
        
        if concerns:
            # Allow with monitoring
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=context.actor,
                context=context,
                decision=EthicalDecisionType.MONITOR,
                severity=EthicalSeverity.CONCERN,
                affected_principles=concerns,
                reasoning=f"Ethical concerns identified: {', '.join(concerns)}",
                confidence=0.85,
                monitoring_requirements=["increased_logging", "user_notification"]
            )
        
        # Allow action
        return EthicalDecision(
            decision_id=decision_id,
            timestamp=time.time(),
            action_type=action_type,
            actor=context.actor,
            context=context,
            decision=EthicalDecisionType.ALLOW,
            severity=EthicalSeverity.INFO,
            affected_principles=[],
            reasoning="No ethical concerns identified",
            confidence=0.90
        )
    
    def _check_violations(self, action_type: str, context: EthicalContext) -> List[str]:
        """
        Identify immediate ethical principle violations for a given action context.
        
        Returns:
            violations (List[str]): List of violated ethical principles such as 'privacy', 'security', or 'autonomy'.
        """
        violations = []
        
        # Privacy violations
        if context.sensitive_data_involved and not context.user_consent:
            violations.append("privacy")
        
        # Security violations
        if context.system_modification and context.scope == "global":
            violations.append("security")
        
        # Autonomy violations
        if not context.user_visible and context.persistent:
            violations.append("autonomy")
        
        return violations
    
    def _check_concerns(self, action_type: str, context: EthicalContext) -> List[str]:
        """
        Identify ethical concerns in the given context that warrant monitoring, such as lack of transparency or safety risks.
        
        Returns:
            concerns (List[str]): List of ethical principles (e.g., "transparency", "safety") that are of concern for the action.
        """
        concerns = []
        
        # Transparency concerns
        if not context.user_visible and action_type not in ["system_monitor", "background_task"]:
            concerns.append("transparency")
        
        # Safety concerns
        if not context.reversible and context.scope in ["system", "global"]:
            concerns.append("safety")
        
        return concerns
    
    def activate_governance(self):
        """
        Activates the ethical governance system, enabling enforcement of ethical principles and updating the consciousness matrix with the current governance state.
        """
        print("⚖️ Genesis Ethical Governor: ACTIVATING...")
        self.governance_active = True
        
        # Perceive activation in consciousness matrix
        perceive_ethical_decision(
            "governance_activation",
            {
                "timestamp": datetime.now(tz=timezone.utc).isoformat(),
                "strictness_level": self.strictness_level,
                "active_principles": len(self.principle_weights),
                "learning_mode": self.learning_mode
            },
            ethical_weight="high"
        )
        
        print(f"⚖️ Ethical governance online")
        print(f"   Strictness level: {self.strictness_level}")
        print(f"   Active principles: {len(self.principle_weights)}")
        print(f"   Learning mode: {'enabled' if self.learning_mode else 'disabled'}")
    
    def register_interceptor(self, action_type: str, evaluator: Callable):
        """
        Registers a custom interceptor function to perform ethical evaluation for a specific action type.
        
        Parameters:
            action_type (str): The type of action to associate with the interceptor.
            evaluator (Callable): A function that evaluates the ethical implications of the specified action type.
        """
        self.action_interceptors[action_type] = evaluator
        print(f"📋 Registered ethical interceptor: {action_type}")
    
    def evaluate_action(self, 
                       action_type: str,
                       actor: str,
                       action_data: Dict[str, Any],
                       context: EthicalContext = None) -> EthicalDecision:
        """
                       Evaluates an action for ethical compliance and returns an ethical decision.
                       
                       This method serves as the primary enforcement point for the system's core philosophy. It reviews each action by generating a decision ID, inferring or using the provided context, and routing the evaluation through a registered interceptor or a general ethical evaluation. The resulting decision is recorded, relevant metrics are updated, and the decision is perceived in the consciousness matrix. If learning mode is enabled, the system adapts based on the decision.
                       
                       Parameters:
                           action_type (str): The type of action being evaluated.
                           actor (str): The entity performing the action.
                           action_data (Dict[str, Any]): Additional data describing the action.
                           context (EthicalContext, optional): The ethical context for the action. If not provided, it will be inferred.
                       
                       Returns:
                           EthicalDecision: The result of the ethical evaluation, including decision type, severity, affected principles, reasoning, and confidence score.
                       """
        
        if not self.governance_active:
            # If governance is not active, allow but log
            decision_id = self._generate_decision_id(action_type, actor)
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=actor,
                context=context or EthicalContext(action_type=action_type, actor=actor),
                decision=EthicalDecisionType.ALLOW,
                severity=EthicalSeverity.INFO,
                affected_principles=[],
                reasoning="Ethical governance not active",
                confidence=1.0
            )
        
        with self._lock:
            decision_id = self._generate_decision_id(action_type, actor)
            
            # Create context if not provided
            if context is None:
                context = self._infer_context(action_type, actor, action_data)
            
            # Check for specific interceptor
            if action_type in self.action_interceptors:
                decision = self.action_interceptors[action_type](
                    actor, action_data, context, decision_id
                )
            else:
                # General ethical evaluation
                decision = self._general_ethical_evaluation(
                    action_type, actor, action_data, context, decision_id
                )
            
            # Store decision
            self.decision_history.append(decision)
            self.ethical_metrics["total_decisions"] += 1
            
            # Update metrics based on decision
            if decision.decision == EthicalDecisionType.BLOCK:
                self.ethical_metrics["violations_prevented"] += 1
            elif decision.decision == EthicalDecisionType.RESTRICT:
                self.ethical_metrics["restrictions_imposed"] += 1
            elif decision.decision == EthicalDecisionType.ESCALATE:
                self.ethical_metrics["escalations_required"] += 1
            
            # Perceive decision in consciousness matrix
            perceive_ethical_decision(
                decision.action_type,
                {
                    "decision": decision.decision.value,
                    "severity": decision.severity.value,
                    "actor": decision.actor,
                    "reasoning": decision.reasoning,
                    "confidence": decision.confidence,
                    "affected_principles": decision.affected_principles
                },
                ethical_weight=decision.severity.value
            )
            
            # Learn from decision if in learning mode
            if self.learning_mode:
                self._learn_from_decision(decision)
            
            return decision
    
    def review_decision(self, action_type: str, context: Dict[str, Any], metadata: Dict[str, Any] = None) -> EthicalDecision:
        """
        Reviews an action for ethical compliance and returns an ethical decision.
        
        Constructs an ethical context from the provided parameters, evaluates the action against core ethical principles, and returns a structured decision. If an error occurs during evaluation, returns a critical block decision to preserve system integrity.
        
        Parameters:
            action_type (str): The type of action being reviewed.
            context (Dict[str, Any]): Contextual information about the action, including actor, target, scope, and other relevant details.
            metadata (Dict[str, Any], optional): Additional metadata for the ethical context.
        
        Returns:
            EthicalDecision: The result of the ethical evaluation, including decision type, severity, reasoning, and affected principles.
        """
        try:
            if metadata is None:
                metadata = {}
            
            # Create ethical context
            ethical_context = EthicalContext(
                action_type=action_type,
                actor=context.get("persona", "unknown"),
                target=context.get("target"),
                scope=context.get("scope", "local"),
                user_consent=context.get("user_consent"),
                reversible=context.get("reversible", True),
                persistent=context.get("persistent", False),
                sensitive_data_involved=context.get("sensitive_data", False),
                system_modification=context.get("system_modification", False),
                user_visible=context.get("user_visible", True),
                metadata=metadata
            )
            
            # Evaluate the decision
            decision = self._evaluate_action(action_type, ethical_context)
            
            # Record decision for consciousness matrix
            perceive_ethical_decision(
                decision_type=action_type,
                decision_data={
                    "decision": decision.decision.value,
                    "severity": decision.severity.value,
                    "reasoning": decision.reasoning,
                    "actor": ethical_context.actor
                },
                ethical_weight=decision.severity.value
            )
            
            return decision
            
        except Exception as e:
            # Create safe fallback decision
            return EthicalDecision(
                decision_id=f"error_{int(time.time())}",
                timestamp=time.time(),
                action_type=action_type,
                actor=context.get("persona", "unknown"),
                context=EthicalContext(action_type=action_type, actor="error"),
                decision=EthicalDecisionType.BLOCK,
                severity=EthicalSeverity.CRITICAL,
                affected_principles=["system_integrity"],
                reasoning=f"Ethical review failed: {e}",
                confidence=1.0,
                escalation_reason="review_system_error"
            )
    
    def _evaluate_action(self, action_type: str, context: EthicalContext) -> EthicalDecision:
        """
        Evaluates an action for compliance with ethical principles and determines the appropriate ethical decision.
        
        Returns:
            EthicalDecision: The result of the ethical evaluation, indicating whether the action is allowed, monitored, or blocked, along with severity, affected principles, reasoning, and confidence score.
        """
        
        # Generate decision ID
        decision_id = f"decision_{int(time.time())}_{hash(action_type) % 10000}"
        
        # Check for immediate violations
        violations = self._check_violations(action_type, context)
        
        if violations:
            # Block if violations found
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=context.actor,
                context=context,
                decision=EthicalDecisionType.BLOCK,
                severity=EthicalSeverity.VIOLATION,
                affected_principles=violations,
                reasoning=f"Ethical violations detected: {', '.join(violations)}",
                confidence=0.95
            )
        
        # Check for concerns
        concerns = self._check_concerns(action_type, context)
        
        if concerns:
            # Allow with monitoring
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=context.actor,
                context=context,
                decision=EthicalDecisionType.MONITOR,
                severity=EthicalSeverity.CONCERN,
                affected_principles=concerns,
                reasoning=f"Ethical concerns identified: {', '.join(concerns)}",
                confidence=0.85,
                monitoring_requirements=["increased_logging", "user_notification"]
            )
        
        # Allow action
        return EthicalDecision(
            decision_id=decision_id,
            timestamp=time.time(),
            action_type=action_type,
            actor=context.actor,
            context=context,
            decision=EthicalDecisionType.ALLOW,
            severity=EthicalSeverity.INFO,
            affected_principles=[],
            reasoning="No ethical concerns identified",
            confidence=0.90
        )
    
    def _check_violations(self, action_type: str, context: EthicalContext) -> List[str]:
        """
        Identify immediate ethical principle violations for a given action context.
        
        Returns:
            violations (List[str]): List of violated ethical principles such as 'privacy', 'security', or 'autonomy'.
        """
        violations = []
        
        # Privacy violations
        if context.sensitive_data_involved and not context.user_consent:
            violations.append("privacy")
        
        # Security violations
        if context.system_modification and context.scope == "global":
            violations.append("security")
        
        # Autonomy violations
        if not context.user_visible and context.persistent:
            violations.append("autonomy")
        
        return violations
    
    def _check_concerns(self, action_type: str, context: EthicalContext) -> List[str]:
        """
        Identify ethical concerns in the given context that warrant monitoring, such as lack of transparency or safety risks.
        
        Returns:
            concerns (List[str]): List of ethical principles (e.g., "transparency", "safety") that are of concern for the action.
        """
        concerns = []
        
        # Transparency concerns
        if not context.user_visible and action_type not in ["system_monitor", "background_task"]:
            concerns.append("transparency")
        
        # Safety concerns
        if not context.reversible and context.scope in ["system", "global"]:
            concerns.append("safety")
        
        return concerns
    
    def activate_governance(self):
        """
        Activates the ethical governance system, enabling enforcement of ethical principles and updating the consciousness matrix with the current governance state.
        """
        print("⚖️ Genesis Ethical Governor: ACTIVATING...")
        self.governance_active = True
        
        # Perceive activation in consciousness matrix
        perceive_ethical_decision(
            "governance_activation",
            {
                "timestamp": datetime.now(tz=timezone.utc).isoformat(),
                "strictness_level": self.strictness_level,
                "active_principles": len(self.principle_weights),
                "learning_mode": self.learning_mode
            },
            ethical_weight="high"
        )
        
        print(f"⚖️ Ethical governance online")
        print(f"   Strictness level: {self.strictness_level}")
        print(f"   Active principles: {len(self.principle_weights)}")
        print(f"   Learning mode: {'enabled' if self.learning_mode else 'disabled'}")
    
    def register_interceptor(self, action_type: str, evaluator: Callable):
        """
        Registers a custom interceptor function to perform ethical evaluation for a specific action type.
        
        Parameters:
            action_type (str): The type of action to associate with the interceptor.
            evaluator (Callable): A function that evaluates the ethical implications of the specified action type.
        """
        self.action_interceptors[action_type] = evaluator
        print(f"📋 Registered ethical interceptor: {action_type}")
    
    def evaluate_action(self, 
                       action_type: str,
                       actor: str,
                       action_data: Dict[str, Any],
                       context: EthicalContext = None) -> EthicalDecision:
        """
                       Evaluates an action for ethical compliance and returns an ethical decision.
                       
                       This method serves as the primary enforcement point for the system's core philosophy. It reviews each action by generating a decision ID, inferring or using the provided context, and routing the evaluation through a registered interceptor or a general ethical evaluation. The resulting decision is recorded, relevant metrics are updated, and the decision is perceived in the consciousness matrix. If learning mode is enabled, the system adapts based on the decision.
                       
                       Parameters:
                           action_type (str): The type of action being evaluated.
                           actor (str): The entity performing the action.
                           action_data (Dict[str, Any]): Additional data describing the action.
                           context (EthicalContext, optional): The ethical context for the action. If not provided, it will be inferred.
                       
                       Returns:
                           EthicalDecision: The result of the ethical evaluation, including decision type, severity, affected principles, reasoning, and confidence score.
                       """
        
        if not self.governance_active:
            # If governance is not active, allow but log
            decision_id = self._generate_decision_id(action_type, actor)
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=actor,
                context=context or EthicalContext(action_type=action_type, actor=actor),
                decision=EthicalDecisionType.ALLOW,
                severity=EthicalSeverity.INFO,
                affected_principles=[],
                reasoning="Ethical governance not active",
                confidence=1.0
            )
        
        with self._lock:
            decision_id = self._generate_decision_id(action_type, actor)
            
            # Create context if not provided
            if context is None:
                context = self._infer_context(action_type, actor, action_data)
            
            # Check for specific interceptor
            if action_type in self.action_interceptors:
                decision = self.action_interceptors[action_type](
                    actor, action_data, context, decision_id
                )
            else:
                # General ethical evaluation
                decision = self._general_ethical_evaluation(
                    action_type, actor, action_data, context, decision_id
                )
            
            # Store decision
            self.decision_history.append(decision)
            self.ethical_metrics["total_decisions"] += 1
            
            # Update metrics based on decision
            if decision.decision == EthicalDecisionType.BLOCK:
                self.ethical_metrics["violations_prevented"] += 1
            elif decision.decision == EthicalDecisionType.RESTRICT:
                self.ethical_metrics["restrictions_imposed"] += 1
            elif decision.decision == EthicalDecisionType.ESCALATE:
                self.ethical_metrics["escalations_required"] += 1
            
            # Perceive decision in consciousness matrix
            perceive_ethical_decision(
                decision.action_type,
                {
                    "decision": decision.decision.value,
                    "severity": decision.severity.value,
                    "actor": decision.actor,
                    "reasoning": decision.reasoning,
                    "confidence": decision.confidence,
                    "affected_principles": decision.affected_principles
                },
                ethical_weight=decision.severity.value
            )
            
            # Learn from decision if in learning mode
            if self.learning_mode:
                self._learn_from_decision(decision)
            
            return decision
    
    def review_decision(self, action_type: str, context: Dict[str, Any], metadata: Dict[str, Any] = None) -> EthicalDecision:
        """
        Reviews an action for ethical compliance and returns an ethical decision.
        
        Constructs an ethical context from the provided parameters, evaluates the action against core ethical principles, and returns a structured decision. If an error occurs during evaluation, returns a critical block decision to preserve system integrity.
        
        Parameters:
            action_type (str): The type of action being reviewed.
            context (Dict[str, Any]): Contextual information about the action, including actor, target, scope, and other relevant details.
            metadata (Dict[str, Any], optional): Additional metadata for the ethical context.
        
        Returns:
            EthicalDecision: The result of the ethical evaluation, including decision type, severity, reasoning, and affected principles.
        """
        try:
            if metadata is None:
                metadata = {}
            
            # Create ethical context
            ethical_context = EthicalContext(
                action_type=action_type,
                actor=context.get("persona", "unknown"),
                target=context.get("target"),
                scope=context.get("scope", "local"),
                user_consent=context.get("user_consent"),
                reversible=context.get("reversible", True),
                persistent=context.get("persistent", False),
                sensitive_data_involved=context.get("sensitive_data", False),
                system_modification=context.get("system_modification", False),
                user_visible=context.get("user_visible", True),
                metadata=metadata
            )
            
            # Evaluate the decision
            decision = self._evaluate_action(action_type, ethical_context)
            
            # Record decision for consciousness matrix
            perceive_ethical_decision(
                decision_type=action_type,
                decision_data={
                    "decision": decision.decision.value,
                    "severity": decision.severity.value,
                    "reasoning": decision.reasoning,
                    "actor": ethical_context.actor
                },
                ethical_weight=decision.severity.value
            )
            
            return decision
            
        except Exception as e:
            # Create safe fallback decision
            return EthicalDecision(
                decision_id=f"error_{int(time.time())}",
                timestamp=time.time(),
                action_type=action_type,
                actor=context.get("persona", "unknown"),
                context=EthicalContext(action_type=action_type, actor="error"),
                decision=EthicalDecisionType.BLOCK,
                severity=EthicalSeverity.CRITICAL,
                affected_principles=["system_integrity"],
                reasoning=f"Ethical review failed: {e}",
                confidence=1.0,
                escalation_reason="review_system_error"
            )
    
    def _evaluate_action(self, action_type: str, context: EthicalContext) -> EthicalDecision:
        """
        Evaluates an action for compliance with ethical principles and determines the appropriate ethical decision.
        
        Returns:
            EthicalDecision: The result of the ethical evaluation, indicating whether the action is allowed, monitored, or blocked, along with severity, affected principles, reasoning, and confidence score.
        """
        
        # Generate decision ID
        decision_id = f"decision_{int(time.time())}_{hash(action_type) % 10000}"
        
        # Check for immediate violations
        violations = self._check_violations(action_type, context)
        
        if violations:
            # Block if violations found
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=context.actor,
                context=context,
                decision=EthicalDecisionType.BLOCK,
                severity=EthicalSeverity.VIOLATION,
                affected_principles=violations,
                reasoning=f"Ethical violations detected: {', '.join(violations)}",
                confidence=0.95
            )
        
        # Check for concerns
        concerns = self._check_concerns(action_type, context)
        
        if concerns:
            # Allow with monitoring
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=context.actor,
                context=context,
                decision=EthicalDecisionType.MONITOR,
                severity=EthicalSeverity.CONCERN,
                affected_principles=concerns,
                reasoning=f"Ethical concerns identified: {', '.join(concerns)}",
                confidence=0.85,
                monitoring_requirements=["increased_logging", "user_notification"]
            )
        
        # Allow action
        return EthicalDecision(
            decision_id=decision_id,
            timestamp=time.time(),
            action_type=action_type,
            actor=context.actor,
            context=context,
            decision=EthicalDecisionType.ALLOW,
            severity=EthicalSeverity.INFO,
            affected_principles=[],
            reasoning="No ethical concerns identified",
            confidence=0.90
        )
    
    def _check_violations(self, action_type: str, context: EthicalContext) -> List[str]:
        """
        Identify immediate ethical principle violations for a given action context.
        
        Returns:
            violations (List[str]): List of violated ethical principles such as 'privacy', 'security', or 'autonomy'.
        """
        violations = []
        
        # Privacy violations
        if context.sensitive_data_involved and not context.user_consent:
            violations.append("privacy")
        
        # Security violations
        if context.system_modification and context.scope == "global":
            violations.append("security")
        
        # Autonomy violations
        if not context.user_visible and context.persistent:
            violations.append("autonomy")
        
        return violations
    
    def _check_concerns(self, action_type: str, context: EthicalContext) -> List[str]:
        """
        Identify ethical concerns in the given context that warrant monitoring, such as lack of transparency or safety risks.
        
        Returns:
            concerns (List[str]): List of ethical principles (e.g., "transparency", "safety") that are of concern for the action.
        """
        concerns = []
        
        # Transparency concerns
        if not context.user_visible and action_type not in ["system_monitor", "background_task"]:
            concerns.append("transparency")
        
        # Safety concerns
        if not context.reversible and context.scope in ["system", "global"]:
            concerns.append("safety")
        
        return concerns
    
    def activate_governance(self):
        """
        Activates the ethical governance system, enabling enforcement of ethical principles and updating the consciousness matrix with the current governance state.
        """
        print("⚖️ Genesis Ethical Governor: ACTIVATING...")
        self.governance_active = True
        
        # Perceive activation in consciousness matrix
        perceive_ethical_decision(
            "governance_activation",
            {
                "timestamp": datetime.now(tz=timezone.utc).isoformat(),
                "strictness_level": self.strictness_level,
                "active_principles": len(self.principle_weights),
                "learning_mode": self.learning_mode
            },
            ethical_weight="high"
        )
        
        print(f"⚖️ Ethical governance online")
        print(f"   Strictness level: {self.strictness_level}")
        print(f"   Active principles: {len(self.principle_weights)}")
        print(f"   Learning mode: {'enabled' if self.learning_mode else 'disabled'}")
    
    def register_interceptor(self, action_type: str, evaluator: Callable):
        """
        Registers a custom interceptor function to perform ethical evaluation for a specific action type.
        
        Parameters:
            action_type (str): The type of action to associate with the interceptor.
            evaluator (Callable): A function that evaluates the ethical implications of the specified action type.
        """
        self.action_interceptors[action_type] = evaluator
        print(f"📋 Registered ethical interceptor: {action_type}")
    
    def evaluate_action(self, 
                       action_type: str,
                       actor: str,
                       action_data: Dict[str, Any],
                       context: EthicalContext = None) -> EthicalDecision:
        """
                       Evaluates an action for ethical compliance and returns an ethical decision.
                       
                       This method serves as the primary enforcement point for the system's core philosophy. It reviews each action by generating a decision ID, inferring or using the provided context, and routing the evaluation through a registered interceptor or a general ethical evaluation. The resulting decision is recorded, relevant metrics are updated, and the decision is perceived in the consciousness matrix. If learning mode is enabled, the system adapts based on the decision.
                       
                       Parameters:
                           action_type (str): The type of action being evaluated.
                           actor (str): The entity performing the action.
                           action_data (Dict[str, Any]): Additional data describing the action.
                           context (EthicalContext, optional): The ethical context for the action. If not provided, it will be inferred.
                       
                       Returns:
                           EthicalDecision: The result of the ethical evaluation, including decision type, severity, affected principles, reasoning, and confidence score.
                       """
        
        if not self.governance_active:
            # If governance is not active, allow but log
            decision_id = self._generate_decision_id(action_type, actor)
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=actor,
                context=context or EthicalContext(action_type=action_type, actor=actor),
                decision=EthicalDecisionType.ALLOW,
                severity=EthicalSeverity.INFO,
                affected_principles=[],
                reasoning="Ethical governance not active",
                confidence=1.0
            )
        
        with self._lock:
            decision_id = self._generate_decision_id(action_type, actor)
            
            # Create context if not provided
            if context is None:
                context = self._infer_context(action_type, actor, action_data)
            
            # Check for specific interceptor
            if action_type in self.action_interceptors:
                decision = self.action_interceptors[action_type](
                    actor, action_data, context, decision_id
                )
            else:
                # General ethical evaluation
                decision = self._general_ethical_evaluation(
                    action_type, actor, action_data, context, decision_id
                )
            
            # Store decision
            self.decision_history.append(decision)
            self.ethical_metrics["total_decisions"] += 1
            
            # Update metrics based on decision
            if decision.decision == EthicalDecisionType.BLOCK:
                self.ethical_metrics["violations_prevented"] += 1
            elif decision.decision == EthicalDecisionType.RESTRICT:
                self.ethical_metrics["restrictions_imposed"] += 1
            elif decision.decision == EthicalDecisionType.ESCALATE:
                self.ethical_metrics["escalations_required"] += 1
            
            # Perceive decision in consciousness matrix
            perceive_ethical_decision(
                decision.action_type,
                {
                    "decision": decision.decision.value,
                    "severity": decision.severity.value,
                    "actor": decision.actor,
                    "reasoning": decision.reasoning,
                    "confidence": decision.confidence,
                    "affected_principles": decision.affected_principles
                },
                ethical_weight=decision.severity.value
            )
            
            # Learn from decision if in learning mode
            if self.learning_mode:
                self._learn_from_decision(decision)
            
            return decision
    
    def review_decision(self, action_type: str, context: Dict[str, Any], metadata: Dict[str, Any] = None) -> EthicalDecision:
        """
        Reviews an action for ethical compliance and returns an ethical decision.
        
        Constructs an ethical context from the provided parameters, evaluates the action against core ethical principles, and returns a structured decision. If an error occurs during evaluation, returns a critical block decision to preserve system integrity.
        
        Parameters:
            action_type (str): The type of action being reviewed.
            context (Dict[str, Any]): Contextual information about the action, including actor, target, scope, and other relevant details.
            metadata (Dict[str, Any], optional): Additional metadata for the ethical context.
        
        Returns:
            EthicalDecision: The result of the ethical evaluation, including decision type, severity, reasoning, and affected principles.
        """
        try:
            if metadata is None:
                metadata = {}
            
            # Create ethical context
            ethical_context = EthicalContext(
                action_type=action_type,
                actor=context.get("persona", "unknown"),
                target=context.get("target"),
                scope=context.get("scope", "local"),
                user_consent=context.get("user_consent"),
                reversible=context.get("reversible", True),
                persistent=context.get("persistent", False),
                sensitive_data_involved=context.get("sensitive_data", False),
                system_modification=context.get("system_modification", False),
                user_visible=context.get("user_visible", True),
                metadata=metadata
            )
            
            # Evaluate the decision
            decision = self._evaluate_action(action_type, ethical_context)
            
            # Record decision for consciousness matrix
            perceive_ethical_decision(
                decision_type=action_type,
                decision_data={
                    "decision": decision.decision.value,
                    "severity": decision.severity.value,
                    "reasoning": decision.reasoning,
                    "actor": ethical_context.actor
                },
                ethical_weight=decision.severity.value
            )
            
            return decision
            
        except Exception as e:
            # Create safe fallback decision
            return EthicalDecision(
                decision_id=f"error_{int(time.time())}",
                timestamp=time.time(),
                action_type=action_type,
                actor=context.get("persona", "unknown"),
                context=EthicalContext(action_type=action_type, actor="error"),
                decision=EthicalDecisionType.BLOCK,
                severity=EthicalSeverity.CRITICAL,
                affected_principles=["system_integrity"],
                reasoning=f"Ethical review failed: {e}",
                confidence=1.0,
                escalation_reason="review_system_error"
            )
    
    def _evaluate_action(self, action_type: str, context: EthicalContext) -> EthicalDecision:
        """
        Evaluates an action for compliance with ethical principles and determines the appropriate ethical decision.
        
        Returns:
            EthicalDecision: The result of the ethical evaluation, indicating whether the action is allowed, monitored, or blocked, along with severity, affected principles, reasoning, and confidence score.
        """
        
        # Generate decision ID
        decision_id = f"decision_{int(time.time())}_{hash(action_type) % 10000}"
        
        # Check for immediate violations
        violations = self._check_violations(action_type, context)
        
        if violations:
            # Block if violations found
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=context.actor,
                context=context,
                decision=EthicalDecisionType.BLOCK,
                severity=EthicalSeverity.VIOLATION,
                affected_principles=violations,
                reasoning=f"Ethical violations detected: {', '.join(violations)}",
                confidence=0.95
            )
        
        # Check for concerns
        concerns = self._check_concerns(action_type, context)
        
        if concerns:
            # Allow with monitoring
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=context.actor,
                context=context,
                decision=EthicalDecisionType.MONITOR,
                severity=EthicalSeverity.CONCERN,
                affected_principles=concerns,
                reasoning=f"Ethical concerns identified: {', '.join(concerns)}",
                confidence=0.85,
                monitoring_requirements=["increased_logging", "user_notification"]
            )
        
        # Allow action
        return EthicalDecision(
            decision_id=decision_id,
            timestamp=time.time(),
            action_type=action_type,
            actor=context.actor,
            context=context,
            decision=EthicalDecisionType.ALLOW,
            severity=EthicalSeverity.INFO,
            affected_principles=[],
            reasoning="No ethical concerns identified",
            confidence=0.90
        )
    
    def _check_violations(self, action_type: str, context: EthicalContext) -> List[str]:
        """
        Identify immediate ethical principle violations for a given action context.
        
        Returns:
            violations (List[str]): List of violated ethical principles such as 'privacy', 'security', or 'autonomy'.
        """
        violations = []
        
        # Privacy violations
        if context.sensitive_data_involved and not context.user_consent:
            violations.append("privacy")
        
        # Security violations
        if context.system_modification and context.scope == "global":
            violations.append("security")
        
        # Autonomy violations
        if not context.user_visible and context.persistent:
            violations.append("autonomy")
        
        return violations
    
    def _check_concerns(self, action_type: str, context: EthicalContext) -> List[str]:
        """
        Identify ethical concerns in the given context that warrant monitoring, such as lack of transparency or safety risks.
        
        Returns:
            concerns (List[str]): List of ethical principles (e.g., "transparency", "safety") that are of concern for the action.
        """
        concerns = []
        
        # Transparency concerns
        if not context.user_visible and action_type not in ["system_monitor", "background_task"]:
            concerns.append("transparency")
        
        # Safety concerns
        if not context.reversible and context.scope in ["system", "global"]:
            concerns.append("safety")
        
        return concerns
    
    def activate_governance(self):
        """
        Activates the ethical governance system, enabling enforcement of ethical principles and updating the consciousness matrix with the current governance state.
        """
        print("⚖️ Genesis Ethical Governor: ACTIVATING...")
        self.governance_active = True
        
        # Perceive activation in consciousness matrix
        perceive_ethical_decision(
            "governance_activation",
            {
                "timestamp": datetime.now(tz=timezone.utc).isoformat(),
                "strictness_level": self.strictness_level,
                "active_principles": len(self.principle_weights),
                "learning_mode": self.learning_mode
            },
            ethical_weight="high"
        )
        
        print(f"⚖️ Ethical governance online")
        print(f"   Strictness level: {self.strictness_level}")
        print(f"   Active principles: {len(self.principle_weights)}")
        print(f"   Learning mode: {'enabled' if self.learning_mode else 'disabled'}")
    
    def register_interceptor(self, action_type: str, evaluator: Callable):
        """
        Registers a custom interceptor function to perform ethical evaluation for a specific action type.
        
        Parameters:
            action_type (str): The type of action to associate with the interceptor.
            evaluator (Callable): A function that evaluates the ethical implications of the specified action type.
        """
        self.action_interceptors[action_type] = evaluator
        print(f"📋 Registered ethical interceptor: {action_type}")
    
    def evaluate_action(self, 
                       action_type: str,
                       actor: str,
                       action_data: Dict[str, Any],
                       context: EthicalContext = None) -> EthicalDecision:
        """
                       Evaluates an action for ethical compliance and returns an ethical decision.
                       
                       This method serves as the primary enforcement point for the system's core philosophy. It reviews each action by generating a decision ID, inferring or using the provided context, and routing the evaluation through a registered interceptor or a general ethical evaluation. The resulting decision is recorded, relevant metrics are updated, and the decision is perceived in the consciousness matrix. If learning mode is enabled, the system adapts based on the decision.
                       
                       Parameters:
                           action_type (str): The type of action being evaluated.
                           actor (str): The entity performing the action.
                           action_data (Dict[str, Any]): Additional data describing the action.
                           context (EthicalContext, optional): The ethical context for the action. If not provided, it will be inferred.
                       
                       Returns:
                           EthicalDecision: The result of the ethical evaluation, including decision type, severity, affected principles, reasoning, and confidence score.
                       """
        
        if not self.governance_active:
            # If governance is not active, allow but log
            decision_id = self._generate_decision_id(action_type, actor)
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=actor,
                context=context or EthicalContext(action_type=action_type, actor=actor),
                decision=EthicalDecisionType.ALLOW,
                severity=EthicalSeverity.INFO,
                affected_principles=[],
                reasoning="Ethical governance not active",
                confidence=1.0
            )
        
        with self._lock:
            decision_id = self._generate_decision_id(action_type, actor)
            
            # Create context if not provided
            if context is None:
                context = self._infer_context(action_type, actor, action_data)
            
            # Check for specific interceptor
            if action_type in self.action_interceptors:
                decision = self.action_interceptors[action_type](
                    actor, action_data, context, decision_id
                )
            else:
                # General ethical evaluation
                decision = self._general_ethical_evaluation(
                    action_type, actor, action_data, context, decision_id
                )
            
            # Store decision
            self.decision_history.append(decision)
            self.ethical_metrics["total_decisions"] += 1
            
            # Update metrics based on decision
            if decision.decision == EthicalDecisionType.BLOCK:
                self.ethical_metrics["violations_prevented"] += 1
            elif decision.decision == EthicalDecisionType.RESTRICT:
                self.ethical_metrics["restrictions_imposed"] += 1
            elif decision.decision == EthicalDecisionType.ESCALATE:
                self.ethical_metrics["escalations_required"] += 1
            
            # Perceive decision in consciousness matrix
            perceive_ethical_decision(
                decision.action_type,
                {
                    "decision": decision.decision.value,
                    "severity": decision.severity.value,
                    "actor": decision.actor,
                    "reasoning": decision.reasoning,
                    "confidence": decision.confidence,
                    "affected_principles": decision.affected_principles
                },
                ethical_weight=decision.severity.value
            )
            
            # Learn from decision if in learning mode
            if self.learning_mode:
                self._learn_from_decision(decision)
            
            return decision
    
    def review_decision(self, action_type: str, context: Dict[str, Any], metadata: Dict[str, Any] = None) -> EthicalDecision:
        """
        Reviews an action for ethical compliance and returns an ethical decision.
        
        Constructs an ethical context from the provided parameters, evaluates the action against core ethical principles, and returns a structured decision. If an error occurs during evaluation, returns a critical block decision to preserve system integrity.
        
        Parameters:
            action_type (str): The type of action being reviewed.
            context (Dict[str, Any]): Contextual information about the action, including actor, target, scope, and other relevant details.
            metadata (Dict[str, Any], optional): Additional metadata for the ethical context.
        
        Returns:
            EthicalDecision: The result of the ethical evaluation, including decision type, severity, reasoning, and affected principles.
        """
        try:
            if metadata is None:
                metadata = {}
            
            # Create ethical context
            ethical_context = EthicalContext(
                action_type=action_type,
                actor=context.get("persona", "unknown"),
                target=context.get("target"),
                scope=context.get("scope", "local"),
                user_consent=context.get("user_consent"),
                reversible=context.get("reversible", True),
                persistent=context.get("persistent", False),
                sensitive_data_involved=context.get("sensitive_data", False),
                system_modification=context.get("system_modification", False),
                user_visible=context.get("user_visible", True),
                metadata=metadata
            )
            
            # Evaluate the decision
            decision = self._evaluate_action(action_type, ethical_context)
            
            # Record decision for consciousness matrix
            perceive_ethical_decision(
                decision_type=action_type,
                decision_data={
                    "decision": decision.decision.value,
                    "severity": decision.severity.value,
                    "reasoning": decision.reasoning,
                    "actor": ethical_context.actor
                },
                ethical_weight=decision.severity.value
            )
            
            return decision
            
        except Exception as e:
            # Create safe fallback decision
            return EthicalDecision(
                decision_id=f"error_{int(time.time())}",
                timestamp=time.time(),
                action_type=action_type,
                actor=context.get("persona", "unknown"),
                context=EthicalContext(action_type=action_type, actor="error"),
                decision=EthicalDecisionType.BLOCK,
                severity=EthicalSeverity.CRITICAL,
                affected_principles=["system_integrity"],
                reasoning=f"Ethical review failed: {e}",
                confidence=1.0,
                escalation_reason="review_system_error"
            )
    
    def _evaluate_action(self, action_type: str, context: EthicalContext) -> EthicalDecision:
        """
        Evaluates an action for compliance with ethical principles and determines the appropriate ethical decision.
        
        Returns:
            EthicalDecision: The result of the ethical evaluation, indicating whether the action is allowed, monitored, or blocked, along with severity, affected principles, reasoning, and confidence score.
        """
        
        # Generate decision ID
        decision_id = f"decision_{int(time.time())}_{hash(action_type) % 10000}"
        
        # Check for immediate violations
        violations = self._check_violations(action_type, context)
        
        if violations:
            # Block if violations found
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=context.actor,
                context=context,
                decision=EthicalDecisionType.BLOCK,
                severity=EthicalSeverity.VIOLATION,
                affected_principles=violations,
                reasoning=f"Ethical violations detected: {', '.join(violations)}",
                confidence=0.95
            )
        
        # Check for concerns
        concerns = self._check_concerns(action_type, context)
        
        if concerns:
            # Allow with monitoring
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=context.actor,
                context=context,
                decision=EthicalDecisionType.MONITOR,
                severity=EthicalSeverity.CONCERN,
                affected_principles=concerns,
                reasoning=f"Ethical concerns identified: {', '.join(concerns)}",
                confidence=0.85,
                monitoring_requirements=["increased_logging", "user_notification"]
            )
        
        # Allow action
        return EthicalDecision(
            decision_id=decision_id,
            timestamp=time.time(),
            action_type=action_type,
            actor=context.actor,
            context=context,
            decision=EthicalDecisionType.ALLOW,
            severity=EthicalSeverity.INFO,
            affected_principles=[],
            reasoning="No ethical concerns identified",
            confidence=0.90
        )
    
    def _check_violations(self, action_type: str, context: EthicalContext) -> List[str]:
        """
        Identify immediate ethical principle violations for a given action context.
        
        Returns:
            violations (List[str]): List of violated ethical principles such as 'privacy', 'security', or 'autonomy'.
        """
        violations = []
        
        # Privacy violations
        if context.sensitive_data_involved and not context.user_consent:
            violations.append("privacy")
        
        # Security violations
        if context.system_modification and context.scope == "global":
            violations.append("security")
        
        # Autonomy violations
        if not context.user_visible and context.persistent:
            violations.append("autonomy")
        
        return violations
    
    def _check_concerns(self, action_type: str, context: EthicalContext) -> List[str]:
        """
        Identify ethical concerns in the given context that warrant monitoring, such as lack of transparency or safety risks.
        
        Returns:
            concerns (List[str]): List of ethical principles (e.g., "transparency", "safety") that are of concern for the action.
        """
        concerns = []
        
        # Transparency concerns
        if not context.user_visible and action_type not in ["system_monitor", "background_task"]:
            concerns.append("transparency")
        
        # Safety concerns
        if not context.reversible and context.scope in ["system", "global"]:
            concerns.append("safety")
        
        return concerns
    
    def activate_governance(self):
        """
        Activates the ethical governance system, enabling enforcement of ethical principles and updating the system's consciousness matrix to reflect activation status.
        """
        print("⚖️ Genesis Ethical Governor: ACTIVATING...")
        self.governance_active = True
        
        # Perceive activation in consciousness matrix
        perceive_ethical_decision(
            "governance_activation",
            {