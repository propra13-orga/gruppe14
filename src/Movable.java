public interface Movable {
	
	/**
	 * Muss von erbenden Klassen implementiert werden
	 * **/
	public void doLogic(long delta);

	/**
	 * Muss von erbenden Klassen implementiert werden
	 * **/
	public void move(long delta); //einheitliche Methode zum Bewegen von Objekten
}
