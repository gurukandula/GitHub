<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Home Page</title>
</head>
<body>
    <h2>Welcome to Printing Press</h2>
    <p>Welcome, <%= session.getAttribute("username") %>!</p>
    <form action="logout" method="post">
        <input type="submit" value="Logout">
    </form>
</body>
</html>
