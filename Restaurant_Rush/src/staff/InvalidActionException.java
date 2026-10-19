package staff;

//for any rules broken
public class InvalidActionException extends RuntimeException{
    public InvalidActionException(String reason){
        super(reason);
    }
}
