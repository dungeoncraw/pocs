from api import db
from ..models import account_model


def list_accounts():
    accounts = account_model.Account.query.all()
    return accounts


def get_account(id):
    account = account_model.Account.query.filter_by(id=id).first()
    return account

def register_account(account):
    account_db = account_model.Account(name=account.name, description=account.description, balance=account.balance)
    db.session.add(account_db)
    db.session.commit()
    return account_db

def update_account(account, updated_data):
    account.name = updated_data.name
    account.description = updated_data.description
    account.balance = updated_data.balance
    db.session.commit()
    return account

def delete_account(account):
    db.session.delete(account)
    db.session.commit()
