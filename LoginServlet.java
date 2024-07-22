import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DynamoDbClient dynamoDbClient;

    @Override
    public void init() throws ServletException {
        Region region = Region.US_WEST_2; // Change to your region
        dynamoDbClient = DynamoDbClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (checkUserCredentials(username, password)) {
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            response.sendRedirect("home.jsp");
        } else {
            request.setAttribute("message", "Invalid username or password");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    private boolean checkUserCredentials(String username, String password) {
        HashMap<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put("username", AttributeValue.builder().s(username).build());

        GetItemRequest request = GetItemRequest.builder()
            .key(keyToGet)
            .tableName("users")
            .build();

        try {
            GetItemResponse response = dynamoDbClient.getItem(request);
            Map<String, AttributeValue> returnedItem = response.item();
            if (returnedItem != null && returnedItem.containsKey("password")) {
                String storedPassword = returnedItem.get("password").s();
                return storedPassword.equals(password);
            }
        } catch (DynamoDbException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void destroy() {
        dynamoDbClient.close();
    }
}