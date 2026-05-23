case class GuitarSpecs(
                        guitarType: GuitarType,
                        bodyWood: Wood,
                        neckWood: Wood,
                        fretboardWood: Wood,
                        pickups: List[Pickup],
                        bridge: Bridge,
                        finish: Finish,
                        stringGauge: StringGauge,
                        numberOfFrets: Int,
                        leftHanded: Boolean
                      )