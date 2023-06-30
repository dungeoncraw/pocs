from flask_restful import Resource
from ..schemas import operation_schema
from flask import request, make_response, jsonify
from ..entities import operation
from ..services imiport operation_service
from api import api

class OperationList(Resource):
    def get(self):
        operations = operation_service.list_operations()
        os = operation_schema.OperationSchema(many=True)
        return make_response(os.jsonify(operations), 200)

    def post(self):
        os = operation_schema.OperationSchema()
        invalid = os.validate(request.json)
        if invalid:
            return make_response(jsonify(invalid), 400)
        
        name = request.json["name"]
        description = request.json["description"]
        value = request.json["value"]
        type = request.json["type"]

        operation_new = operation.Operation(
                    name=name,
                    description=description,
                    value=value,
                    type=type
                )
        result = operation_service.register_operation(operation_new)
        return make_response(os.jsonify(result), 201)
    

class OperationDetail(Resource):
    def get(self, id):
        operation = operation_service.operation_by_id(id)
        if operation is None:
            return make_response(jsonify("Operation not found", 404))
        os = operation_schema.OperationSchema()
        return make_response(os.jsonify(operation), 200)

api.add_resource(OperationList, "/operations")
api.add_resource(OperationDetail, "operations/<int:id>")
