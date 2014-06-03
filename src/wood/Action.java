package wood;
/**
 * Результаты хода персонажа
 */
public enum Action {
	// Персонаж с указанным именем не найден.
	// Такое происходит при ошибке ввода имени или в случае если персонаж умер.
	WoodmanNotFound,
	// В результате хода персонаж умирает;
	Dead,
	// Персонажу не удалось совершить ход.
	// Вероятно, по направлению хода была стена.
	Fail,
	// Успешный ход персонажа.
	Ok,
	// Успешный ход персонажа, после которого у персонажа добавляется еще одна жизнь.
	Life,
	// Персонаж достиг финиша.
	Finish
}