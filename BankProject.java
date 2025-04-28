import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;


public class BankProject extends Application {

    private TextField activeField;
    private Account[] accounts = new Account[10];
    private int accountCnt = 0;
    private Account currentAccount = null;
    private Stage primaryStage;
    private BorderPane mainRoot;
    private VBox buttonLeft;
    private VBox buttonRight;

    private Button[] leftButtons = new Button[3];
    private Button[] rightButtons = new Button[3];

    private BorderPane createMainMenu() {

        BorderPane centerPane = new BorderPane();

        VBox optionLeft = new VBox(105);
        optionLeft.setPadding(new Insets(40));

        VBox optionRight = new VBox(105);
        optionRight.setPadding(new Insets(40));

        Label[] leftLabels = {new Label("DEPOSIT AMOUNT "), new Label("WITHDRAW AMOUNT "), new Label("BALANCE ENQUIRY ")};

        Label[] rightLabels = {new Label("DELETE ACCOUNT "), new Label("NEW ACCOUNT "), new Label("EXIT ")};

        for (Label label : leftLabels) {
            label.setFont(Font.font(24));
            label.setStyle("-fx-background-color:lightgray;");
            optionLeft.getChildren().add(label);
        }

        for (Label label : rightLabels) {
            label.setFont(Font.font(24));
            label.setStyle("-fx-background-color:lightgray;");
            optionRight.getChildren().add(label);
        }

        centerPane.setLeft(optionLeft);
        centerPane.setRight(optionRight);

        VBox newButtonLeft = new VBox(80);
        newButtonLeft.setPadding(new Insets(15));
        VBox newButtonRight = new VBox(80);
        newButtonRight.setPadding(new Insets(15));

        for (int i = 0; i < 3; i++) {
            leftButtons[i] = createSideButton();
            rightButtons[i] = createSideButton();
            newButtonLeft.getChildren().add(leftButtons[i]);
            newButtonRight.getChildren().add(rightButtons[i]);
        }

        setButtonActions();

        HBox hBox = new HBox(10, buttonLeft, centerPane, buttonRight);
        hBox.setAlignment(Pos.CENTER);

        return new BorderPane(hBox);
    }

    private void setButtonActions() {

        leftButtons[0].setText("->");
        leftButtons[0].setOnAction(event -> {
            depositAmountPage();
        });

        leftButtons[1].setText("->");
        leftButtons[1].setOnAction(event -> {
            withdrawAmountPage();
        });

        leftButtons[2].setText("->");
        leftButtons[2].setOnAction(event -> {
            balanceEnquiryPage();
        });

        rightButtons[0].setText("<-");
        rightButtons[0].setOnAction(event -> {
            deleteAccountPage();
        });

        rightButtons[1].setText("<-");
        rightButtons[1].setOnAction(event -> {
            newAccountPage();
        });

        rightButtons[2].setText("<-");
        rightButtons[2].setOnAction(event -> {
            primaryStage.close();
        });

    }


    private void depositAmountPage() {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);

        Label label = new Label("Enter amount to deposit:");
        label.setFont(Font.font(25));

        TextField amountField = new TextField();
        amountField.setPrefSize(130,70);
        amountField.setPromptText("Amount");
        amountField.setStyle("-fx-font-size: 20px;");

        Button depositButton = new Button("Deposit");
        depositButton.setFont(Font.font(20));

        depositButton.setOnAction(event -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    showError("Amount must be greater than 0!");
                } else {
                    currentAccount.deposit(amount);
                    showInfo("Deposited " + amount + "$ successfully");
                    mainRoot.setCenter(createMainMenu());
                }
            } catch (NumberFormatException e) {
                showError("Invalid amount!");
            }
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event1 -> {
                VBox mainMenuWithKeypad = new VBox(30, createMainMenu());
                mainMenuWithKeypad.setAlignment(Pos.CENTER);
                mainRoot.setCenter(mainMenuWithKeypad);
            });
            pause.play();
        });

        box.getChildren().addAll(label, amountField, depositButton);
        mainRoot.setCenter(box);
    }


    private void withdrawAmountPage() {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);

        Label label = new Label("Enter amount to withdraw:");
        label.setFont(Font.font(25));

        TextField amountField = new TextField();
        amountField.setPrefSize(130,70);
        amountField.setPromptText("Amount");
        amountField.setStyle("-fx-font-size: 20px;");

        Button withdrawButton = new Button("Withdraw");
        withdrawButton.setFont(Font.font(20));

        withdrawButton.setOnAction(event -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    showError("Amount must be greater than 0!");
                } else if (amount > currentAccount.getBalance()) {
                    showError("Insufficient Balance!");
                } else {
                    currentAccount.withdraw(amount);
                    showInfo("Withdrew " + amount + "$ successfully");
                    mainRoot.setCenter(createMainMenu()); // بازگشت به منو
                }
            } catch (NumberFormatException e) {
                showError("Invalid amount!");
            }
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event1 -> {
                VBox mainMenuWithKeypad = new VBox(30, createMainMenu());
                mainMenuWithKeypad.setAlignment(Pos.CENTER);
                mainRoot.setCenter(mainMenuWithKeypad);
            });
            pause.play();
        });

        box.getChildren().addAll(label, amountField, withdrawButton);
        mainRoot.setCenter(box);

    }

    private void balanceEnquiryPage() {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        Label label = new Label("Your Balance: " + currentAccount.getBalance() + "$");
        label.setFont(Font.font(25));
        box.getChildren().add(label);
        mainRoot.setCenter(box);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
            VBox mainMenuWithKeypad = new VBox(30, createMainMenu());
            mainMenuWithKeypad.setAlignment(Pos.CENTER);
            mainRoot.setCenter(mainMenuWithKeypad);
        });
        pause.play();
    }

    private void deleteAccountPage() {
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
        mainRoot.setCenter(box);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
            VBox mainMenuWithKeypad = new VBox(30, createMainMenu());
            mainMenuWithKeypad.setAlignment(Pos.CENTER);
            mainRoot.setCenter(mainMenuWithKeypad);
        });
        pause.play();
    }

    private void newAccountPage() {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);

        TextField card = new TextField();
        card.setPrefSize(250,100);
        card.setFont(Font.font(25));
        card.setPromptText("New card number: ");
        TextField pass = new TextField();
        pass.setPrefSize(250,100);
        pass.setFont(Font.font(25));
        pass.setPromptText("New password: ");

        Button createAccount = new Button("CREATE");
        createAccount.setFont(Font.font(20));

        createAccount.setOnAction(event -> {
            if (card.getText().isEmpty() || pass.getText().isEmpty()) {
                showError("Fields cannot be empty!");
            } else {
                addAccount(card.getText(), pass.getText(), 0);
                showInfo("Account created successfully");
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(event1 -> {
                    VBox mainMenuWithKeypad = new VBox(30, createMainMenu());
                    mainMenuWithKeypad.setAlignment(Pos.CENTER);
                    mainRoot.setCenter(mainMenuWithKeypad);
                });
                pause.play();
            }
        });
        Label label=new Label("Create new account");
        label.setFont(Font.font(25));
        box.getChildren().addAll(label, card, pass, createAccount);
        mainRoot.setCenter(box);

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

    private GridPane createKeypad(TextField cardNumber, TextField passNumber) {

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10));
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setStyle("-fx-background-color:lightgray;");


        String[][] layout = {
                {"7", "8", "9", "CANCLE"},
                {"4", "5", "6", "CLEAR"},
                {"1", "2", "3", ""},
                {"#", "0", "*", "ENTER"}
        };

        String[][] colors = {
                {"#b3b3b3", "#b3b3b3", "#b3b3b3", "#cc3333"},
                {"#b3b3b3", "#b3b3b3", "#b3b3b3", "#ffe666"},
                {"#b3b3b3", "#b3b3b3", "#b3b3b3", "#b3b3b3",},
                {"#b3b3b3", "#b3b3b3", "#b3b3b3", "#336633"}
        };
        for (int i = 0; i < layout.length; i++) {
            for (int j = 0; j < layout[i].length; j++) {
                String text = layout[i][j];
                Button button = new Button(text);
                button.setStyle("-fx-background-color : " + colors[i][j] + ";" + "-fx-text-fill:white;");
                button.setFont(Font.font(27));
                button.setPrefSize(150, 70);
                grid.add(button, j, i + 1);


                button.setOnAction(event -> {
                    if (button.getText().equals("CANCLE")) {
                        cardNumber.clear();
                        passNumber.clear();
                    } else if (button.getText().equals("CLEAR")) {
                        activeField.clear();
                    } else if (button.getText().equals("ENTER")) {
                        String card = cardNumber.getText();
                        String pass = passNumber.getText();

                        currentAccount = findAccount(card, pass);

                        if (currentAccount != null) {
                            mainRoot.setCenter(createMainMenu());
                            mainRoot.setLeft(buttonLeft);
                            mainRoot.setRight(buttonRight);
                            mainRoot.setBottom(makeKeyboardShow());
                        } else {
                            showError("Login failed!");
                        }
                    } else {
                        String currentText = activeField.getText();
                        activeField.setText(currentText + button.getText());
                    }
                });

            }
        }
        return grid;
    }

    private GridPane makeKeyboardShow() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10));
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setStyle("-fx-background-color:lightgray;");


        String[][] layout = {
                {"7", "8", "9", "CANCLE"},
                {"4", "5", "6", "CLEAR"},
                {"1", "2", "3", ""},
                {"#", "0", "*", "ENTER"}
        };

        String[][] colors = {
                {"#b3b3b3", "#b3b3b3", "#b3b3b3", "#cc3333"},
                {"#b3b3b3", "#b3b3b3", "#b3b3b3", "#ffe666"},
                {"#b3b3b3", "#b3b3b3", "#b3b3b3", "#b3b3b3",},
                {"#b3b3b3", "#b3b3b3", "#b3b3b3", "#336633"}
        };
        for (int i = 0; i < layout.length; i++) {
            for (int j = 0; j < layout[i].length; j++) {
                String text = layout[i][j];
                Button button = new Button(text);
                button.setStyle("-fx-background-color : " + colors[i][j] + ";" + "-fx-text-fill:white;");
                button.setFont(Font.font(27));
                button.setPrefSize(150, 70);
                grid.add(button, j, i + 1);
            }
        }
        return grid;
}

    @Override
    public void start(Stage stage) throws Exception {

        primaryStage=stage;

        addAccount("1234","1111",120);
        addAccount("5678","2222",20);
        addAccount("3333","0000",240);

        mainRoot=new BorderPane();

        buttonLeft=new VBox(65);
        buttonLeft.setPadding(new Insets(30,0,0,30));

        buttonRight=new VBox(65);
        buttonRight.setPadding(new Insets(30,30,0,0));

        for (int i=0; i<3; i++){
            leftButtons[i]=createSideButton();
            rightButtons[i]=createSideButton();

            buttonLeft.getChildren().add(leftButtons[i]);
            buttonRight.getChildren().add(rightButtons[i]);
        }

        setButtonActions();

        TextField cardNumber=new TextField();
        cardNumber.setStyle("-fx-background-color:white; -fx-font-size:30px;");
        TextField passNumber=new TextField();
        passNumber.setStyle("-fx-background-color:white; -fx-font-size:30px;");

        Label cardNumLable =new Label("Enter card number :");
        cardNumLable.setStyle("-fx-text-fill:black; -fx-font-size:30px;");
        Label passLable =new Label("Enter card password :");
        passLable.setStyle("-fx-text-fill:black; -fx-font-size:30px;");

        VBox center = new VBox(20, cardNumLable, cardNumber, passLable, passNumber);
        center.setAlignment(Pos.CENTER);

        BorderPane loginPane = new BorderPane();
        loginPane.setLeft(buttonLeft);
        loginPane.setCenter(center);
        loginPane.setRight(buttonRight);
        loginPane.setPadding(new Insets(20));
        loginPane.setStyle("-fx-background-color:lightgray;");

        activeField = cardNumber;
        cardNumber.setOnMouseClicked(e -> activeField = cardNumber);
        passNumber.setOnMouseClicked(e -> activeField = passNumber);

        VBox all = new VBox(50, loginPane, createKeypad(cardNumber, passNumber));
        all.setAlignment(Pos.CENTER);

        mainRoot.setLeft(buttonLeft);
        mainRoot.setRight(buttonRight);
        mainRoot.setCenter(all);

        Scene scene=new Scene(mainRoot,900,800);
        stage.setScene(scene);
        stage.setTitle("BANK");
        stage.show();
    }

    private Button createSideButton() {
        Button button = new Button();
        button.setFont(Font.font(32));
        button.setPrefSize(110, 70);
        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
