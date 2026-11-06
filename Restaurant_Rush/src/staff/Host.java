package staff;

import controller.SeatingController;
import entity.Customer;
import entity.CustomerStatus;

public class Host{
    private final SeatingController seatingController;
    private int lastActionTurn = 0;
    
    public Host(SeatingController seatingController){
        this.seatingController = seatingController;
    }

    public boolean seat(Customer customer, int turn){
        checkHasActedThisTurn(turn);

        if(customer == null){
            throw new InvalidActionException("Invalid customer. Enter valid ID.");
        }


        if(customer.getStatus() != CustomerStatus.WAITING){
            if(customer.getStatus() == CustomerStatus.SEATED ||
                customer.getStatus() == CustomerStatus.EATING ||
                customer.getStatus() == CustomerStatus.READY_TO_PAY){
                    throw new InvalidActionException("Customer " + customer.getCustomerId() + " is already seated.");
                }
            else{
                throw new InvalidActionException("Customer " + customer.getCustomerId() + " is not in the queue.");
            }
            
        }
        if(seatingController.findAvailableTable() == null){
            throw new InvalidActionException("No free table. Try another option.");
        }

        

        seatingController.assignSeat(customer);
        customer.markSeated();
        lastActionTurn = turn;
        return true;
    }

    public void checkHasActedThisTurn(int turn){
        if(turn == lastActionTurn){
            throw new InvalidActionException("The Host has already done a task this turn.");
        }
    }



}
