package ru.skillbranch.devintensive.models

class Bender(var status: Status = Status.NORMAL, var question: Question = Question.NAME) {

    fun askQuestion(): String = when (question) {
        Question.NAME -> Question.NAME.question
        Question.PROFESSION -> Question.PROFESSION.question
        Question.MATERIAL -> Question.MATERIAL.question
        Question.BDAY -> Question.BDAY.question
        Question.SERIAL -> Question.SERIAL.question
        Question.IDLE -> Question.IDLE.question
    }

    fun listenAnswer(answer: String): Pair<String, Triple<Int, Int, Int>> {
        return if (question.isAnswerValid(answer)) {
            if (question.answers.contains(answer.toLowerCase())) {
                question = question.nextQuestion()
                "Отлично - ты справился\n${question.question}" to status.color
            } else {
                val oldStatus = status
                status = status.nextStatus()
                if (oldStatus == status) {
                    status = Status.NORMAL
                    question = Question.NAME
                    "Это неправильный ответ. Давай все по новой\n${question.question}" to status.color
                } else "Это неправильный ответ\n${question.question}" to status.color
            }
        } else {
            "${question.validationErrorMessage}${question.question}" to status.color
        }
    }

    enum class Status(val color: Triple<Int, Int, Int>) {
        NORMAL(Triple(255, 255, 255)),
        WARNING(Triple(255, 120, 0)),
        DANGER(Triple(255, 60, 60)),
        CRITICAL(Triple(255, 0, 0));

        fun nextStatus(): Status {
            return if (this.ordinal < values().lastIndex) {
                values()[this.ordinal + 1]
            } else {
                values()[this.ordinal]
            }
        }
    }

    enum class Question(
        val question: String,
        val answers: List<String>,
        val validationErrorMessage: String
    ) {
        NAME(
            "Как меня зовут?",
            listOf("бендер", "bender"),
            "Имя должно начинаться с заглавной буквы\n"
        ) {
            override fun isAnswerValid(answer: String): Boolean = answer[0].isUpperCase()

            override fun nextQuestion(): Question = PROFESSION
        },
        PROFESSION(
            "Назови мою профессию?",
            listOf("сгибальщик", "bender"),
            "Профессия должна начинаться со строчной буквы\n"
        ) {
            override fun isAnswerValid(answer: String): Boolean = answer[0].isLowerCase()

            override fun nextQuestion(): Question = MATERIAL
        },
        MATERIAL(
            "Из чего я сделан?",
            listOf("металл", "дерево", "metal", "iron", "wood"),
            "Материал не должен содержать цифр\n"
        ) {
            override fun isAnswerValid(answer: String): Boolean = answer.contains("^\\D*$".toRegex())

            override fun nextQuestion(): Question = BDAY
        },
        BDAY(
            "Когда меня создали?",
            listOf("2993"),
            "Год моего рождения должен содержать только цифры\n"
        ) {
            override fun isAnswerValid(answer: String): Boolean = answer.contains("^\\d*$".toRegex())

            override fun nextQuestion(): Question = SERIAL
        },
        SERIAL(
            "Мой серийный номер?",
            listOf("2716057"),
            "Серийный номер содержит только цифры, и их 7\n"
        ) {
            override fun isAnswerValid(answer: String): Boolean = answer.contains("^\\d{7}$".toRegex())

            override fun nextQuestion(): Question = IDLE
        },
        IDLE("На этом все, вопросов больше нет", listOf(),"") {
            override fun isAnswerValid(answer: String): Boolean = false

            override fun nextQuestion(): Question = IDLE
        };

        abstract fun nextQuestion(): Question

        abstract fun isAnswerValid(answer: String): Boolean
    }
}