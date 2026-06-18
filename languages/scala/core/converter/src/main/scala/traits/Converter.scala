package traits

trait Converter[In, Out] {
  type Error
  def convert(input: In): Either[List[Error], Out]
}
