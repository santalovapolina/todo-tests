1. Одиночка Singleton 

Конфигурация
Хранилище данных
Хранилище токенов юзеров

2. Строитель Builder 

Lombok - @Builder 
RequestSpecBuilder 
given().. when() ..then()

Builder -> fields (id, name, age) -> build()

setStatus()
.setId()
.setText

3. Декоратор 

декорируем < Функциональность < декорируем

@Mobile 
@Web("1980x1020")

4. Стратегия 

Стратегия проверки, что id уже занят #1
Cтратегия проверки, что text должен быть меньше 5000 символов #2

assertThat()
.strategy(#1)

assertThat()
.strategy(#2)

5. Фасад 

фасад запросов 

юзер
список туду задач
менеджер 

много сущность для тестовых данных 
// 

6. Фабрика 

Созданию запросов 
Созданию Page Object
