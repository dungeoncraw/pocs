// receive an value, unit, measurment system of origin, measurment system of destination and unit destination
const conversion = (value, unitOrigin, unitDestination) => {
  const conversionValue = measurmentTable[unitOrigin][unitDestination];
  return Number(value) * conversionValue;
}

const unit = 0.3937;

const measurmentTable = {
  centimiter: {
    inches: unit,
    feet: 0.0328,
    yard: 0.0109
  },
  inches: {
    centimiter: unit
  }
}
