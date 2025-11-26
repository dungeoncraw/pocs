package com.tetokeguii.day20

abstract class Animal {
  def name: String
}

abstract class Pet extends Animal

class Cat extends Pet {
  def name: String = "Cat"
}
class Dog extends Pet {
  def name: String = "Dog"
}

class Lion extends Animal {
  def name: String = "Lion"
}
