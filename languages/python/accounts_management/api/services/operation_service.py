from api import db
from ..models import operation_model


def list_operations():
    operations = operation_model.Operation.query.all()
    return operations

def operation_by_id(id):
    operation = operation_model.Operation.query.filter_by(id=id).first()
    return operation

def register_operation(operation):
    operation_db = operation_model.Operation(
                name=operation.name,
                description=operation.description,
                value=operation.value,
                type=operation.type
            )
    db.session.add(operation_db)
    db.session.commit()
    return operation_db
