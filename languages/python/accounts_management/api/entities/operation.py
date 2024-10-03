class Operation():
    def __init__(self, name, description, value, type):
        self.__name = name
        self.__description = description
        self.__value = value
        self.__type = type

    @property
    def name(self):
        return self.__name

    @name.setter
    def name(self, name):
        self.__name = name

    @property
    def description(self):
        return self.__description

    @description.setter
    def description(self, description):
        self.__description = description
   
    @property
    def value(self):
        return self.__value

    @value.setter
    def value(self, value):
        self.__value = value

    @property
    def type(self):
        return self.__type

    @type.setter
    def type(self, type):
        self.__type = type
