# Shopping Platform Discount Service

This project is a RESTful service designed for a shopping platform to calculate product prices based on configurable discount policies. 

## Features
- Retrieve product details by UUID through a REST API endpoint.
- Calculate total price for a product and quantity, applying the appropriate discount.
- Configurable discount policies defined in `application.yml`, adjustable without code changes. Go to `discount` section in application.yml to configure the discount policies.

## Prerequisites
- **JDK 17**
- **Docker**


## Building the Project
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd products-platform
   
2. Build the project using Gradle:
   ```bash
   ./gradlew bootJar
   ```
   
3. Run the application:
   ```bash
   docker compose up --build
   ```
   
## Configure discount policies
Discount policies are configurable via src/main/resources/application.yml. Example configuration:

```yaml
discount:
   combiningStrategy: HIGHEST
   configurations:
      - discountType: PERCENTAGE
        discountPercentage: 0.10
      - discountType: QUANTITY
        rules:
           - min: 1
             max: 5
             discountPercentage: 0.10
           - min: 6
             max: 10
             discountPercentage: 0.20
           - min: 11
             max: ~
             discountPercentage: 0.20
```
- Quantity-based discounts: Defined by min, max, and discountPercentage. `discountType: QUANTITY`
- Percentage-based discount: Fixed rate applied to the total price. `discountType: PERCENTAGE`
- Discount combination: By default, only the **HIGHEST** discount applies. You can change the strategy to one of the following:
  - **HIGHEST**: Only the highest discount applies.
  - **COMBINED**: Both discounts are applied to the total price.

## API Endpoints
### Retrieve Product by ID
**GET** `/products/{id}`

Retrieves product details by its UUID.

- **Path Parameters**:
    - `id` (UUID): The unique identifier of the product.
- **Response**:
    - `200 OK`: Returns the product details.
      ```json
      {
        "id": "product-uuid",
        "money": {
          "amount": 100.00,
          "currency": "USD"
        }
      }
      ```
    - `404 Not Found`: If the product is not found.

---

### Calculate Total Price for a Product
**POST** `/products/{id}/calculate-price`

Calculates the total price for a product based on the quantity and applicable discounts.

- **Path Parameters**:
    - `id` (UUID): The unique identifier of the product.
- **Request Body**:
    - `quantity` (Long, required): The number of items to calculate the price for. Must be greater than or equal to 0.
      ```json
      {
        "quantity": 55
      }
      ```
- **Response**:
    - `200 OK`: Returns the total price after applying discounts.
      ```json
      {
        "amount": 90.0,
        "currency": "USD"
      }
      ```
    - `400 Bad Request`: If the quantity is invalid.
    - `404 Not Found`: If the product is not found.

## TESTING
Use created product: `550e8400-e29b-41d4-a716-446655440000` to test the API