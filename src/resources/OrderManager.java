package resources;
import Order.java;
import java.util.Random;
public class OrderManager {
    public List<Order> activeOrder;
    public List<Recipe> avalaiableRecipe;
    public float spawnCooldown;
    public float spawnTimer;

    public OrderManager(List<Recipe> avalaiableRecipe, float spawnCooldown){
        this.activeOrder = new List<>();
        this.avalaiableRecipe = avalaiableRecipe;
        this.spawnCooldown = spawnCooldown;
        this.spawnTimer = 0;
    }
    public update (){

    }
    public spawnOrder(){
        Random random = new Random();
        Recipe randomRecipe = avalaiableRecipe.get(random.nextInt(avalaiableRecipe.size()));
        int maxDuration = 20;
        int minDuration = 10;
        
        int randomOrderDuration = random.nextInt(max - min + 1) + min; 
        Order orderSpawn = new Order(randomRecipe, randomOrderDuration);
        this.activeOrder.add(orderSpawn);
    }

    public boolean checkOrder(Dish dish){
        for (Order order:activeOrder){
            if (order.validateDish(dish)){
                return true;
            }
        }
        return false;
    }


}
