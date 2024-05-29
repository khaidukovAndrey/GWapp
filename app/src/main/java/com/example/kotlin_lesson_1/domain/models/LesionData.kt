package com.example.kotlin_lesson_1.domain.models

class LesionData(){
    val lesions = mapOf(
        0 to "Актинический кератоз",
        1 to "Базалиома",
        2 to "Доброкачественный лихеноидный кератоз",
        3 to "Дерматофиброма",
        4 to "Меланоцитарный невус",
        5 to "Пиогенная гранулема",
        6 to "Меланома"
    )
    val descriptions = mapOf(
        0 to "Это широко распространённое заболевание с медленным, неуклонно прогрессирующим течением, возникновение которого спровоцировано воздействием на кожу прямых солнечных лучей. Возможно как саморазрешение процесса, так и перерождение в рак.",
        1 to "Это злокачественная опухоль кожи, развивающаяся из клеток эпидермиса. В отличие от других злокачественных опухолей базалиома практически не дает метастазов.",
        2 to "Это доброкачественное поражение кожи, связанное с выраженными воспалительными изменениями,напоминающее по внешнему виду красный плоский лишай",
        3 to "Это доброкачественная опухоль кожи, образованная зрелыми волокнами соединительной ткани. Дерматофиброма отличается медленным ростом и отсутствием склонности к злокачественному перерождению.",
        4 to "Меланоцитарные невусы, или родинки — это доброкачественные разрастания невусных клеток. ",
        5 to "Это доброкачественное новообразование кожи или слизистых оболочек сосудистой природы.",
        6 to "Это злокачественная опухоль, возникающая в результате атипического перерождения и размножения пигментных клеток. Характеризуется быстрым распространением опухолевых клеток по всему организму."
    )

    val recomendation = mapOf(
        0 to "Во избежании перерождения кератоза в рак следует посетить врача-дерматолога, который назначит вам лечение.",
        1 to "Следует посетить врача-дерматолога, который в индивидуальном порядке подберет лечебную тактику в зависимости от характеристик базалиомы.",
        2 to "Рекомендуется посещение врача-дерматолога для точной постановки диагноза. После постановки диагноза лихеноидного кератоза дальнейшая терапия не рекомендована,так как заболевание носит доброкачественный характер.",
        3 to "Рекомендуется посещение врача-дерматолога для точной постановки диагноза. ",
        4 to "Если родинка вызвает подозрения, следует обратиться к врачу-дерматологу.",
        5 to "Если уплотнение не подвергается обратному развитию самопроизвольно, показано хирургическое лечение.",
        6 to "Следует посетить врача-дерматолога для точной постановки диагноза. Меланома диагностируется по данным исследования мазка-отпечатка, сделанного с ее поверхности. Лечение проводится в зависимости от стадии меланомы и может состоять в хирургическом иссечении образования, удалении лимфатических узлов, иммунотерапии, лучевой терапии и химиотерапии."
    )
}