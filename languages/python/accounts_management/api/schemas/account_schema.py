from api import ma
from ..models import account_model
from marshmallow import fields

class AccountSchema(ma.SQLAlchemyAutoSchema):
    class Meta:
        model = account_model.Account
        load_instance = True
        
        name = fields.String(required=True)
        description = fields.String(required=True)
        balance = fields.Float(required=True)
