"""empty message

Revision ID: 5298d95998f4
Revises: 43b6f0ce4515
Create Date: 2023-06-26 09:22:29.494854

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = '5298d95998f4'
down_revision = '43b6f0ce4515'
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.create_table('operations',
    sa.Column('id', sa.Integer(), autoincrement=True, nullable=True),
    sa.Column('name', sa.String(length=50), nullable=False),
    sa.Column('description', sa.String(length=100), nullable=False),
    sa.Column('value', sa.Float(), nullable=False),
    sa.Column('type', sa.Enum('deposit', 'withdraw', name='operationtypeenum'), nullable=True),
    sa.PrimaryKeyConstraint('id')
    )
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_table('operations')
    # ### end Alembic commands ###