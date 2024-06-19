from flask_restful import Resource
from flask import request, make_response, jsonify
from ..schemas import account_schema
from ..entities import account
from ..services import account_service
from api import api

class AccountList(Resource):
    def post(self):
        cs = account_schema.AccountSchema()
        invalid = cs.validate(request.json)
        if invalid:
            return make_response(jsonify(invalud), 400)
        
        name = request.json["name"]
        description = request.json["description"]
        balance = request.json["balance"]
        account_new = account.Account(name=name, description=description, balance=balance)
        account_result = account_service.register_account(account_new)
        return make_response(cs.jsonify(account_result), 201)

    def get(self):
        accounts = account_service.account_list()
        cs = account_schema.AccountSchema(many=True)
        return make_response(cs.jsonify(accounts), 200)


class AccountDetail(Resource):
    def get(self, id):
        account_details = account_service.get_account(id)
        if account_details is None:
            return make_response(jsonify("Account not found"), 404)
        cs = account_schema.AccountSchema()
        return make_response(cs.jsonify(account_details), 200)

    def put(self, id):
        account_details = account_servie.get_account(id)
        if account_details is None:
            return make_response(jsonify("Account not found", 404))
        cs = account_schema.AccountSchema()
        invalid =  cs.validate(request.json)
        if invalid:
            return make_response(jsonify(invalid), 400)
        name = request.json["name"]
        description = request.json["description"]
        balance = request.json["balance"]
        updated_values = account.Account(name=name, description=description, balance=balance)
        result = account_service.update_account(account_details, updated_values)
        return make_response(cs.jsonify(result), 201)


    def delete(self, id):
        account_details = account_service.get_account(id)
        if account_details is None:
            return make_response(jsonify("Account not found", 404))
        account_service.delete_account(account_details)
        return make_response(jsonify(""), 204)


api.add_resource(AccountList, '/accounts')
api.add_resource(AccountDetail, '/account/<int:id>')
