trait GuitarPart:
  def label: String

enum GuitarModel(val label: String):
  case CustomStrat extends GuitarModel("Custom Strat")
  case LesPaulStyle extends GuitarModel("Les Paul Style")
  case TeleStyle extends GuitarModel("Tele Style")
  case SuperStrat extends GuitarModel("Super Strat")

enum BodyWood(val label: String) extends GuitarPart:
  case AlderBody extends BodyWood("Alder Body")
  case MahoganyBody extends BodyWood("Mahogany Body")
  case AshBody extends BodyWood("Ash Body")
  case BasswoodBody extends BodyWood("Basswood Body")

enum NeckWood(val label: String) extends GuitarPart:
  case MapleNeck extends NeckWood("Maple Neck")
  case MahoganyNeck extends NeckWood("Mahogany Neck")
  case RosewoodNeck extends NeckWood("Rosewood Neck")

enum Pickup(val label: String) extends GuitarPart:
  case SingleCoil extends Pickup("Single Coil Pickup")
  case Humbucker extends Pickup("Humbucker Pickup")
  case P90 extends Pickup("P90 Pickup")

enum Bridge(val label: String) extends GuitarPart:
  case Tremolo extends Bridge("Tremolo Bridge")
  case Fixed extends Bridge("Fixed Bridge")
  case FloydRose extends Bridge("Floyd Rose Bridge")

enum Finish(val label: String) extends GuitarPart:
  case Sunburst extends Finish("Sunburst Finish")
  case Black extends Finish("Black Finish")
  case Natural extends Finish("Natural Finish")
  case Red extends Finish("Red Finish")

enum GuitarOS(val label: String) extends GuitarPart:
  case PassiveAnalog extends GuitarOS("Passive Analog")
  case SmartToneOS extends GuitarOS("Smart Tone OS")
  case StudioOS extends GuitarOS("Studio OS")
  case StageOS extends GuitarOS("Stage OS")