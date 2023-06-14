
# The eye

This project helps to analyse user website access behaviour, providing a structured information about user info and which websites they are navigating.

  
## Installation

There's two ways of running this project, stand-alone dockerized or installing and running django and mongo.

### stand-alone Docker

To run this version you must have Docker and docker-compose installed in your system.
Just clone this repo and at top level folder run the command

```bash
  docker-compose up
```

### Django and Mongo

To run this version you must have python 3.9.7 and mongodb 5.0 installed, so after cloning this repo run these commands at top leve folder

```bash
  pip install -r requirements.txt
  python manage.py runserver 0.0.0.0:8000
```

## API Reference

Boilerplate

#### Get all items

```http
  GET /api/items
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `api_key` | `string` | **Required**. Your API key |

#### Get item

```http
  GET /api/items/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of item to fetch |

#### add(num1, num2)

Takes two numbers and returns the sum.
