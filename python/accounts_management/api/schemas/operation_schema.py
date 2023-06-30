from api import ma
from ..models import operation_model
from marshmallow import fields
from marshmallow_enum import EnumField

class OperationSchema(ma.SQLAlchemyAutoSchema):
    type = EnumField(operation_model.OperationTypeEnum)
    class Meta:
        model = operation_model.Operation
        load_instance = True

        name = fields.String(required=True)
        description = fields.String(required=True)
        value = fields.Float(required=True)
