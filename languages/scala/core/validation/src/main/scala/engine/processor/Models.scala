package engine.processor

case class ConstraintModel(
    annotationType: String,
    parameters: Map[String, Any],
    message: String
)

case class FieldModel(
    name: String,
    `type`: String,
    constraints: List[ConstraintModel],
    isRecordComponent: Boolean,
    isNested: Boolean
)

case class TypeModel(
    packageName: String,
    className: String,
    validatorClassName: String,
    fields: List[FieldModel],
    isRecord: Boolean
)
