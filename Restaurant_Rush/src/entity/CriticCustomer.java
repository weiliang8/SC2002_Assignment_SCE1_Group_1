package entity;

public class CriticCustomer extends Customer {
    private static final int GOOD_REVIEW_MIN = 70;
    private static final int BAD_REVIEW_BELOW = 50;
    private static final int REVIEW_EFFECT = 10;

    public CriticCustomer(int id) {
        super(id, CustomerType.CRITIC, 12);
    }

    @Override
    public int getReviewImpact() {
        if (getSatisfaction() >= GOOD_REVIEW_MIN) {
            return REVIEW_EFFECT;
        }
        if (getSatisfaction() < BAD_REVIEW_BELOW) {
            return -REVIEW_EFFECT;
        }
        return 0;
    }
}
