import Recipe.java;
public class Order {
    public Recipe recipe;
    public float timerRemaining;
    public boolean isCompleted;
    public boolean isFailed;

    /**
     * create new order
     * @param recipe
     * @param timerRemaining
     */
    public Order(Recipe recipe, float timerRemaining){
        this.recipe = recipe;
        this.timerRemaining = timerRemaining;
        this.isCompleted = false;
        this.isFailed = false;
    }

}
