case class Guitar(
                   serialNumber: String,
                   spec: GuitarSpec
                 ):
  def description: String =
    s"""
       |Model: ${spec.model.label}
       |Body Wood: ${spec.bodyWood.label}
       |Neck Wood: ${spec.neckWood.label}
       |Pickup: ${spec.pickup.label}
       |Bridge: ${spec.bridge.label}
       |Finish: ${spec.finish.label}
       |OS: ${spec.os.label}
       |""".stripMargin