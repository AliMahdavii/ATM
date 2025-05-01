import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Optional;


public class BankProject extends Application {

    private TextField activeField;
    private Account[] accounts = new Account[10];
    private int accountCnt = 0;
    private Account currentAccount = null;
    private Stage primaryStage;
    private BorderPane mainRoot;

    private void depositAmountPage() {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #8caeb6;");

        BorderPane container=new BorderPane();
        container.setStyle("-fx-background-color: #8caeb6;");
        container.setTop(makeBackBtn(createDashboardPage()));
        BorderPane.setAlignment(container.getTop(),Pos.TOP_LEFT);
        BorderPane.setMargin(container.getTop(),new Insets(10));
        container.setCenter(box);

        Label label = new Label("Enter amount to deposit:");
        label.setFont(Font.font(25));

        TextField amountField = new TextField();
        amountField.setPrefSize(130, 70);
        amountField.setMaxWidth(250);
        amountField.setPromptText("Amount");
        amountField.setStyle("-fx-font-size: 20px;");

        ContextMenu suggestions = new ContextMenu();
        int[] suggestedAmounts = {10, 20, 50, 100, 200};
        for (int amt : suggestedAmounts) {
            MenuItem item = new MenuItem(String.valueOf(amt)+"$");
            item.setStyle("-fx-background-radius: 10;");
            item.setOnAction(e -> amountField.setText(String.valueOf(amt)));
            suggestions.getItems().add(item);
        }

        amountField.setOnMouseEntered(e -> {
            if (!suggestions.isShowing()) {
                suggestions.show(amountField, amountField.getScene().getWindow().getX() + amountField.localToScene(0, 0).getX() + amountField.getScene().getX() + amountField.getWidth(),
                        amountField.getScene().getWindow().getY() + amountField.localToScene(0, 0).getY() + amountField.getScene().getY());
            }
        });

        Button depositButton = new Button("Deposit");
        depositButton.setFont(Font.font(20));
        depositButton.setStyle("-fx-background-color: #3151b6; -fx-text-fill: white; -fx-background-radius: 10;");
        depositButton.setOnMouseEntered(e -> depositButton.setStyle("-fx-background-color: #74b9ff; -fx-text-fill: white; -fx-background-radius: 10;"));
        depositButton.setOnMouseExited(e -> depositButton.setStyle("-fx-background-color: #3151b6; -fx-text-fill: white; -fx-background-radius: 10;"));

        depositButton.setOnAction(event -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    showError("Amount must be greater than 0!");
                } else if (amount > 200) {
                    showError("You can't deposit more than 200$");
                } else {
                    currentAccount.deposit(amount);
                    showInfo("Deposited " + amount + "$ successfully");

                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(event1 -> {
                        VBox mainMenuWithKeypad = new VBox(30, createDashboardPage());
                        mainMenuWithKeypad.setAlignment(Pos.CENTER);
                        mainRoot.setStyle("-fx-background-color: #8caeb6;");
                        mainRoot.setCenter(mainMenuWithKeypad);
                    });
                    pause.play();
                }
            } catch (NumberFormatException e) {
                showError("Invalid amount!");
            }
        });

        box.getChildren().addAll(label, amountField, depositButton);
        mainRoot.setCenter(container);
    }


    private void withdrawAmountPage() {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #8caeb6;");

        BorderPane container=new BorderPane();
        container.setStyle("-fx-background-color: #8caeb6;");
        container.setTop(makeBackBtn(createDashboardPage()));
        BorderPane.setAlignment(container.getTop(),Pos.TOP_LEFT);
        BorderPane.setMargin(container.getTop(),new Insets(10));
        container.setCenter(box);

        Label label = new Label("Enter amount to withdraw:");
        label.setFont(Font.font(25));

        TextField amountField = new TextField();
        amountField.setPrefSize(130,70);
        amountField.setMaxWidth(250);
        amountField.setPromptText("Amount");
        amountField.setStyle("-fx-font-size: 20px;");

        ContextMenu suggestions = new ContextMenu();
        int[] suggestedAmounts = {10, 20, 50, 100, 200};
        for (int amt : suggestedAmounts) {
            MenuItem item = new MenuItem(String.valueOf(amt)+"$");
            item.setStyle("-fx-background-radius: 10;");
            item.setOnAction(e -> amountField.setText(String.valueOf(amt)));
            suggestions.getItems().add(item);
        }

        amountField.setOnMouseEntered(e -> {
            if (!suggestions.isShowing()) {
                suggestions.show(amountField, amountField.getScene().getWindow().getX() + amountField.localToScene(0, 0).getX() + amountField.getScene().getX() + amountField.getWidth(),
                        amountField.getScene().getWindow().getY() + amountField.localToScene(0, 0).getY() + amountField.getScene().getY());
            }
        });

        Button withdrawButton = new Button("Withdraw");
        withdrawButton.setFont(Font.font(20));
        withdrawButton.setStyle("-fx-background-color: #3151b6; -fx-text-fill: white; -fx-background-radius: 10;");
        withdrawButton.setOnMouseEntered(e -> withdrawButton.setStyle("-fx-background-color: #74b9ff; -fx-text-fill: white; -fx-background-radius: 10;"));
        withdrawButton.setOnMouseExited(e -> withdrawButton.setStyle("-fx-background-color: #3151b6; -fx-text-fill: white; -fx-background-radius: 10;"));

        withdrawButton.setOnAction(event -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    showError("Amount must be greater than 0!");
                } else if (amount>200){
                    showError("You can't withdraw more than 200$");
                } else if (amount > currentAccount.getBalance()) {
                    showError("Insufficient Balance!");
                } else {
                    currentAccount.withdraw(amount);
                    showInfo("Withdrew " + amount + "$ successfully");
                }
            } catch (NumberFormatException e) {
                showError("Invalid amount!");
            }
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event1 -> {
                VBox mainMenuWithKeypad = new VBox(30,createDashboardPage());
                mainMenuWithKeypad.setAlignment(Pos.CENTER);
                mainRoot.setCenter(mainMenuWithKeypad);
            });
            pause.play();
        });

        box.getChildren().addAll(label, amountField, withdrawButton);
        mainRoot.setStyle("-fx-background-color: #8caeb6;");
        mainRoot.setCenter(container);

    }

    private void balanceEnquiryPage() {
        VBox box = new VBox(20);
        box.setStyle("-fx-background-color: #8caeb6;");
        box.setAlignment(Pos.CENTER);
        Label label = new Label("Card number: "+currentAccount.getCardNumber()+"\n\nYour Balance: " + currentAccount.getBalance() + "$");
        label.setFont(Font.font(25));
        box.getChildren().add(label);
        mainRoot.setCenter(box);

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> {
            VBox mainMenuWithKeypad = new VBox(30, createDashboardPage());
            mainMenuWithKeypad.setAlignment(Pos.CENTER);
            mainRoot.setStyle("-fx-background-color: #8caeb6;");
            mainRoot.setCenter(mainMenuWithKeypad);
        });
        pause.play();
    }

    private void deleteAccountPage() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Are you sure you want to delete your account ???");
        Optional<ButtonType> result=alert.showAndWait();

        if (result.isPresent() && result.get()==ButtonType.OK){
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        Label label = new Label("Account deleted successfully");
        label.setFont(Font.font(25));
        box.getChildren().add(label);
        mainRoot.setCenter(box);

        for (int i = 0; i < accountCnt; i++) {
            if (accounts[i] == currentAccount) {
                for (int j = i; j < accountCnt - 1; j++) {
                    accounts[j] = accounts[j + 1];
                }
                accounts[--accountCnt] = null;
                currentAccount = null;
                break;
            }
        }

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
            VBox mainMenu = new VBox(30, createLoginPage());
            mainRoot.setCenter(mainMenu);
        });
        pause.play();
        } else {
            VBox box = new VBox(20);
            box.setAlignment(Pos.CENTER);
            Label label = new Label("Account deletion canceled.");
            label.setFont(Font.font(25));
            box.getChildren().add(label);
            mainRoot.setCenter(box);
            mainRoot.setStyle("-fx-background-color: #8caeb6;");

            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event -> {
                VBox mainMenu = new VBox(30, createDashboardPage());
                mainMenu.setAlignment(Pos.CENTER);
                mainRoot.setStyle("-fx-background-color: #8caeb6;");
                mainRoot.setCenter(mainMenu);
            });
            pause.play();
        }
    }

    private void newAccountPage(boolean flag) {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #8caeb6;");

        BorderPane container=new BorderPane();
        container.setStyle("-fx-background-color: #8caeb6;");
        container.setTop(makeBackBtn(createDashboardPage()));
        BorderPane.setAlignment(container.getTop(),Pos.TOP_LEFT);
        BorderPane.setMargin(container.getTop(),new Insets(10));
        container.setCenter(box);

        TextField card = new TextField();
        card.setPrefSize(250,80);
        card.setMaxWidth(350);
        card.setFont(Font.font(22));
        card.setPromptText("New card number: ");
        TextField pass = new TextField();
        pass.setPrefSize(250,80);
        pass.setMaxWidth(350);
        pass.setFont(Font.font(22));
        pass.setPromptText("New password: ");

        Button createAccount = new Button("CREATE");
        createAccount.setFont(Font.font(20));
        createAccount.setStyle("-fx-background-color: #3151b6; -fx-text-fill: white; -fx-background-radius: 10;");
        createAccount.setOnMouseEntered(e -> createAccount.setStyle("-fx-background-color: #74b9ff; -fx-text-fill: white; -fx-background-radius: 10;"));
        createAccount.setOnMouseExited(e -> createAccount.setStyle("-fx-background-color: #3151b6; -fx-text-fill: white; -fx-background-radius: 10;"));

        createAccount.setOnAction(event -> {
            if (card.getText().isEmpty() || pass.getText().isEmpty()) {
                showError("Fields cannot be empty!");
            } else {
                addAccount(card.getText(), pass.getText(), 0);
                currentAccount=accounts[accountCnt=1];
                showInfo("Account created successfully");
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(event1 -> {
                    VBox mainMenuWithKeypad = new VBox(30, createDashboardPage());
                    mainMenuWithKeypad.setAlignment(Pos.CENTER);
                    mainRoot.setStyle("-fx-background-color: #8caeb6;");
                    if (flag)
                    mainRoot.setCenter(mainMenuWithKeypad);
                    else
                        mainRoot.setCenter(createLoginPage());
                });
                pause.play();
            }
        });
        Label label=new Label("Create new account");
        label.setFont(Font.font(25));
        box.getChildren().addAll(label, card, pass, createAccount);
        mainRoot.setCenter(container);

    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.show();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.show();
    }

    private void addAccount(String cardNumber, String password, double balance) {
        if (accountCnt < accounts.length) {
            accounts[accountCnt++] = new Account(cardNumber, password, balance);
        }
    }

    private Account findAccount(String cardNumber, String password) {
        for (Account account : accounts) {
            if (account != null && account.getCardNumber().equals(cardNumber) && account.getPassword().equals(password))
                return account;
        }
        return null;
    }

    private VBox createLoginPage() {
        VBox loginBox = new VBox(20);
        loginBox.setPrefHeight(800);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPadding(new Insets(50));
        loginBox.setStyle("-fx-background-color: #8caeb6;");

        Label title = new Label("Welcome to SmartBank");
        title.setFont(Font.font( 36));
        title.setStyle("-fx-text-fill: #2d3436;");

        FadeTransition fade = new FadeTransition(Duration.seconds(1.5), title);
        fade.setFromValue(0.8);
        fade.setToValue(1.2);
        fade.setCycleCount(Animation.INDEFINITE);
        fade.setAutoReverse(true);

        TranslateTransition slide = new TranslateTransition(Duration.seconds(1.5), title);
        slide.setByX(15);
        slide.setCycleCount(Animation.INDEFINITE);
        slide.setAutoReverse(true);

        ParallelTransition combined = new ParallelTransition(fade, slide);
        combined.play();

        TextField cardInput = new TextField();
        cardInput.setPromptText("Card Number");
        cardInput.setFont(Font.font(18));
        cardInput.setMaxWidth(300);
        cardInput.setStyle("-fx-background-radius: 10; -fx-padding: 10;");

        TextField passInput = new TextField();
        passInput.setPromptText("Password");
        passInput.setFont(Font.font(18));
        passInput.setMaxWidth(300);
        passInput.setStyle("-fx-background-radius: 10; -fx-padding: 10;");

        Button loginBtn = new Button("Login");
        loginBtn.setFont(Font.font(20));
        loginBtn.setStyle("-fx-background-color: #3151b6; -fx-text-fill: white; -fx-background-radius: 10;");
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle("-fx-background-color: #74b9ff; -fx-text-fill: white; -fx-background-radius: 10;"));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle("-fx-background-color: #3151b6; -fx-text-fill: white; -fx-background-radius: 10;"));

        loginBtn.setOnAction(e -> {
            currentAccount = findAccount(cardInput.getText(), passInput.getText());
            if (currentAccount != null) {
                showInfo("Login successful!");
                mainRoot.setCenter(createDashboardPage());
            } else {
                showError("Login failed!");
            }
        });

        Button createAccountBtn = new Button("CREATE NEW ACCOUNT");
        createAccountBtn.setFont(Font.font(14));
        createAccountBtn.setStyle("-fx-background-color: #3151b6; -fx-text-fill: white; -fx-background-radius: 10;");
        createAccountBtn.setOnMouseEntered(e -> createAccountBtn.setStyle("-fx-background-color: #74b9ff; -fx-text-fill: white; -fx-background-radius: 10;"));
        createAccountBtn.setOnMouseExited(e -> createAccountBtn.setStyle("-fx-background-color: #3151b6; -fx-text-fill: white; -fx-background-radius: 10;"));
        createAccountBtn.setOnAction(event -> newAccountPage(false));

        loginBox.getChildren().addAll(title, cardInput, passInput, loginBtn,createAccountBtn);
        return loginBox;
    }

    private VBox createDashboardPage(){
        VBox dashboard=new VBox(30);
        dashboard.setAlignment(Pos.CENTER);
        dashboard.setPadding(new Insets(40));
        dashboard.setStyle("-fx-background-color: #8caeb6;");

        Label welcome=new Label("Welcome ,"+currentAccount.getCardNumber());
        welcome.setFont(Font.font(28));

        FadeTransition fade = new FadeTransition(Duration.seconds(1.5), welcome);
        fade.setFromValue(0.8);
        fade.setToValue(1.5);
        fade.setCycleCount(Animation.INDEFINITE);
        fade.setAutoReverse(true);

        TranslateTransition slide = new TranslateTransition(Duration.seconds(1.5), welcome);
        slide.setByX(20);
        slide.setCycleCount(Animation.INDEFINITE);
        slide.setAutoReverse(true);

        ParallelTransition combined = new ParallelTransition(fade, slide);
        combined.play();

        Button depositBtn=makeDashboardBtn("DEPOSIT AMOUNT ");
        Button withdrawBtn=makeDashboardBtn("WITHDRAW AMOUNT ");
        Button balanceEnquiryBtn=makeDashboardBtn("BALANCE ENQUIRY ");
        Button deleteAccountBtn=makeDashboardBtn("DELETE ACCOUNT ");
        Button newAccountBtn=makeDashboardBtn("NEW ACCOUNT ");
        Button exitBtn=makeDashboardBtn("EXIT ");

        depositBtn.setOnAction(event -> depositAmountPage());
        withdrawBtn.setOnAction(event -> withdrawAmountPage());
        balanceEnquiryBtn.setOnAction(event -> balanceEnquiryPage());
        deleteAccountBtn.setOnAction(event -> deleteAccountPage());
        newAccountBtn.setOnAction(event -> newAccountPage(true));
        exitBtn.setOnAction(event -> primaryStage.close());

        dashboard.getChildren().addAll(welcome,depositBtn,withdrawBtn,balanceEnquiryBtn,deleteAccountBtn,newAccountBtn,exitBtn);

        return dashboard;
    }

    private Button makeDashboardBtn(String text){
        Button button=new Button(text);
        button.setFont(Font.font(20));
        button.setPrefSize(250,60);
        button.setStyle("-fx-background-color: #3151b6; -fx-text-fill: white; -fx-background-radius: 10;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #74b9ff; -fx-text-fill: white; -fx-background-radius: 10;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #3151b6; -fx-text-fill: white; -fx-background-radius: 10;"));

        return button;
    }

    private Button makeBackBtn(VBox page){
        Button button=new Button("BACK");
        button.setFont(Font.font(15));
        button.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 8;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #b3b3b3; -fx-text-fill: white; -fx-background-radius: 8;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 8;"));
        button.setOnAction(event -> mainRoot.setCenter(page));

        return button;
    }

    @Override
    public void start(Stage stage) throws Exception {

        primaryStage=stage;

        addAccount("1234","1111",120);
        addAccount("5678","2222",20);
        addAccount("3333","0000",240);

        mainRoot=new BorderPane();
        mainRoot.setCenter(createLoginPage());

        Scene scene=new Scene(mainRoot,500,800);
        stage.setScene(scene);
        stage.setTitle("BANK");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}