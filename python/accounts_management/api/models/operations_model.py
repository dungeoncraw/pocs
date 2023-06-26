from api import db
import enum

class OperationTypeEnum(enum.Enum):
    deposit = 1
    withdraw = 2

class Operation(db.Model):
    __tablename__ = "operations"

    id = db.Column(db.Integer, primary_key=True, autoincrement=True, nullable=True)
    name = db.Column(db.String(50), nullable=False)
    description = db.Column(db.String(100), nullable=False)
    value = db.Column(db.Float, nullable=False)
    type = db.Column(db.Enum((OperationTypeEnum), nullable=False))
