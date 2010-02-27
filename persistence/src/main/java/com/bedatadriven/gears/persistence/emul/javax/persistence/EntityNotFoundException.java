package javax.persistence;

public class EntityNotFoundException extends PersistenceException {
	public EntityNotFoundException() {
		super();
	}
	public EntityNotFoundException(String message) {
		super(message);
	}
};

