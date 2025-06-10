package ATM;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class BankProject extends Application {

    private Account[] accounts =loadAccounts();
    private int accountCnt = countAccount(accounts);
    private Account currentAccount = null;
    private Stage primaryStage;
    private BorderPane mainRoot;

    private List<ExchangeRate> exchangeRates= Arrays.asList(
            new ExchangeRate(950_0000,"2025/01/13"),
            new ExchangeRate(400_000,"2023/01/13"),
            new ExchangeRate(100_000,"2022/01/13"),
            new ExchangeRate(65_000,"2021/01/13"),
            new ExchangeRate(40_000,"2020/01/13"),
            new ExchangeRate(1_000,"2004/01/13")
    );

    private void saveAccounts(Account[] accounts) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("accounts.ser"))) {
            oos.writeObject(accounts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Account[] loadAccounts() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("accounts.ser"))) {
            return (Account[]) ois.readObject();
        } catch (Exception e) {
            return new Account[100];
        }
    }

    private int countAccount(Account[] accounts){
        int cnt=0;
        for (Account account:accounts){
            if (account!=null)
                cnt++;
        }
        return cnt;
    }

    private void depositAmountPage() {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #1e1e1e;");

        Label label = new Label("Enter amount to deposit:");
        label.setFont(Font.font(25));
        label.setStyle("-fx-text-fill: #eeeeee;");

        TextField amountField = new TextField();
        amountField.setPrefSize(130, 70);
        amountField.setMaxWidth(290);
        amountField.setPromptText("Amount ($)");
        amountField.setStyle("-fx-font-size: 20px; -fx-background-radius: 40;");
        amountField.setAlignment(Pos.CENTER);

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
        depositButton.setMaxHeight(60);
        depositButton.setMaxWidth(290);
        depositButton.setStyle("-fx-background-color: #3d3dff; -fx-text-fill: #eeeeee; -fx-background-radius: 40;");
        depositButton.setOnMouseEntered(e -> depositButton.setStyle("-fx-background-color: #444444; -fx-text-fill: #ffffff; -fx-background-radius: 40;"));
        depositButton.setOnMouseExited(e -> depositButton.setStyle("-fx-background-color: #3d3dff; -fx-text-fill: #eeeeee; -fx-background-radius: 40;"));

        depositButton.setDefaultButton(true);

        depositButton.setOnKeyPressed(event -> {
            if (event.getCode()==KeyCode.ENTER){
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
                        VBox mainMenu = new VBox(30, createDashboardPage());
                        mainMenu.setAlignment(Pos.CENTER);
                        mainRoot.setStyle("-fx-background-color: #1e1e1e;");
                        mainRoot.setCenter(mainMenu);
                    });
                    pause.play();
                }
            } catch (NumberFormatException e) {
                showError("Invalid amount!");
            }
          }
        });

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
                        VBox mainMenu = new VBox(30, createDashboardPage());
                        mainMenu.setAlignment(Pos.CENTER);
                        mainRoot.setStyle("-fx-background-color: #1e1e1e;");
                        mainRoot.setCenter(mainMenu);
                    });
                    pause.play();
                }
            } catch (NumberFormatException e) {
                showError("Invalid amount!");
            }
        });

        Button backBtn=makeBackBtn(createDashboardPage());

        box.getChildren().addAll(label, amountField, depositButton,backBtn);
        mainRoot.setCenter(box);
    }

    private void withdrawAmountPage() {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #1e1e1e;");

        Label label = new Label("Enter amount to withdraw:");
        label.setFont(Font.font(25));
        label.setStyle("-fx-text-fill: #eeeeee;");

        TextField amountField = new TextField();
        amountField.setPrefSize(130, 70);
        amountField.setMaxWidth(290);
        amountField.setPromptText("Amount ($)");
        amountField.setStyle("-fx-font-size: 20px; -fx-background-radius: 40;");
        amountField.setAlignment(Pos.CENTER);

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
        withdrawButton.setMaxWidth(290);
        withdrawButton.setStyle("-fx-background-color: #3d3dff; -fx-text-fill: #eeeeee; -fx-background-radius: 40;");
        withdrawButton.setOnMouseEntered(e -> withdrawButton.setStyle("-fx-background-color: #444444; -fx-text-fill: #ffffff; -fx-background-radius: 40;"));
        withdrawButton.setOnMouseExited(e -> withdrawButton.setStyle("-fx-background-color: #3d3dff; -fx-text-fill: #eeeeee; -fx-background-radius: 40;"));

        withdrawButton.setDefaultButton(true);

        withdrawButton.setOnKeyPressed(event -> {
            if (event.getCode()==KeyCode.ENTER){
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
                VBox mainMenu = new VBox(30,createDashboardPage());
                mainMenu.setAlignment(Pos.CENTER);
                mainRoot.setCenter(mainMenu);
            });
            pause.play();
            }
        });

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
                VBox mainMenu = new VBox(30,createDashboardPage());
                mainMenu.setAlignment(Pos.CENTER);
                mainRoot.setCenter(mainMenu);
            });
            pause.play();
        });

        Button backBtn=makeBackBtn(createDashboardPage());

        box.getChildren().addAll(label, amountField, withdrawButton,backBtn);
        mainRoot.setStyle("-fx-background-color: #1e1e1e;");
        mainRoot.setCenter(box);

    }

    private void balanceEnquiryPage() {
        VBox box = new VBox(20);
        box.setStyle("-fx-background-color: #1e1e1e;");
        box.setAlignment(Pos.CENTER);
        Label label = new Label("Card number : "+currentAccount.getCardNumber()+"\n\nYour Balance: " + currentAccount.getBalance() + "$");
        label.setFont(Font.font(25));
        label.setStyle("-fx-text-fill: #eeeeee;");
        box.getChildren().add(label);
        mainRoot.setCenter(box);

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> {
            VBox mainMenu = new VBox(30, createDashboardPage());
            mainMenu.setAlignment(Pos.CENTER);
            mainRoot.setStyle("-fx-background-color: #1e1e1e;");
            mainRoot.setCenter(mainMenu);
        });
        pause.play();
    }

    private void deleteAccountPage() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Are you sure you want to delete your account ???");
        Optional<ButtonType> result=alert.showAndWait();

        if (result.isPresent() && result.get()==ButtonType.OK){
        VBox box = new VBox(20);
        box.setStyle("-fx-background-color: #1e1e1e;");
        box.setAlignment(Pos.CENTER);
        Label label = new Label("ATM.Account " + currentAccount.getCardNumber() + " deleted successfully .");
        label.setFont(Font.font(25));
        label.setStyle("-fx-text-fill: #eeeeee;");
        box.getChildren().add(label);
        mainRoot.setCenter(box);

        for (int i = 0; i < accountCnt; i++) {
            if (accounts[i]!=null && accounts[i].getCardNumber().equals(currentAccount.getCardNumber())) {
                for (int j = i; j < accountCnt - 1; j++) {
                    accounts[j] = accounts[j + 1];
                }
                accounts[--accountCnt] = null;
                currentAccount = null;
                break;
            }
        }

        saveAccounts(accounts);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
            VBox mainMenu = new VBox(30, createLoginPage());
            mainMenu.setStyle("-fx-background-color: #1e1e1e;");
            mainRoot.setCenter(mainMenu);
        });
        pause.play();
        } else {
            VBox box = new VBox(20);
            box.setAlignment(Pos.CENTER);
            Label label = new Label("ATM.Account deletion canceled.");
            label.setFont(Font.font(25));
            label.setStyle("-fx-text-fill: #eeeeee;");
            box.getChildren().add(label);
            mainRoot.setCenter(box);
            mainRoot.setStyle("-fx-background-color: #1e1e1e;");

            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event -> {
                VBox mainMenu = new VBox(30, createDashboardPage());
                mainMenu.setAlignment(Pos.CENTER);
                mainRoot.setStyle("-fx-background-color: #1e1e1e;");
                mainRoot.setCenter(mainMenu);
            });
            pause.play();
        }
    }

    private String generateUniqueCardNumber(){
        Random random=new Random();
        String card;
        boolean isUnique;

        do {
            int num=10000000+random.nextInt(90000000);
            card=String.valueOf(num);
            isUnique=true;

            for (int i=0; i<accountCnt; i++){
                if (accounts[i]!=null && accounts[i].getCardNumber().equals(card)){
                    isUnique=false;
                    break;
                }
            }
        } while (!isUnique);
        return card;
    }

    private void newAccountPage(boolean flag) {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #1e1e1e;");

        Label label=new Label("Create new account");
        label.setStyle("-fx-text-fill: #eeeeee;");
        label.setFont(Font.font(25));

        TextField nameField = new TextField();
        nameField.setMaxWidth(350);
        nameField.setFont(Font.font(22));
        nameField.setStyle("-fx-background-radius: 40;");
        nameField.setPromptText("Your name ");
        nameField.setAlignment(Pos.CENTER);

        PasswordField pass = new PasswordField();
        pass.setMaxWidth(350);
        pass.setFont(Font.font(22));
        pass.setStyle("-fx-background-radius: 40;");
        pass.setPromptText("New password ");
        pass.setAlignment(Pos.CENTER);

        setupNavigation(nameField,pass,null);

        Button createAccountBtn = new Button("CREATE");
        createAccountBtn.setFont(Font.font(20));
        createAccountBtn.setMaxWidth(350);
        createAccountBtn.setStyle("-fx-background-color: #3d3dff; -fx-text-fill: #eeeeee; -fx-background-radius: 40;");
        createAccountBtn.setOnMouseEntered(e -> createAccountBtn.setStyle("-fx-background-color: #444444; -fx-text-fill: #ffffff; -fx-background-radius: 40;"));
        createAccountBtn.setOnMouseExited(e -> createAccountBtn.setStyle("-fx-background-color: #3d3dff; -fx-text-fill: #eeeeee; -fx-background-radius: 40;"));

        createAccountBtn.setDefaultButton(true);

        createAccountBtn.setOnKeyPressed(event -> {
            if (event.getCode()==KeyCode.ENTER){
            if (nameField.getText().isEmpty() || pass.getText().isEmpty()) {
                showError("Fields cannot be empty!");
            } else {
                addAccount(generateUniqueCardNumber(), pass.getText(), nameField.getText(), 0);
                currentAccount=accounts[accountCnt-1];
                saveAccounts(accounts);
                showInfo("ATM.Account created successfully"+"\nAccount number -> " + formatCardNumber(currentAccount.getCardNumber()));
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(event1 -> {
                    VBox mainMenu = new VBox(30, createDashboardPage());
                    mainMenu.setAlignment(Pos.CENTER);
                    mainRoot.setStyle("-fx-background-color: #1e1e1e;");
                    if (flag)
                        mainRoot.setCenter(mainMenu);
                    else
                        mainRoot.setCenter(createLoginPage());
                });
                pause.play();
            }
          }
        });

        createAccountBtn.setOnAction(event -> {
            if (nameField.getText().isEmpty() || pass.getText().isEmpty()) {
                showError("Fields cannot be empty!");
            } else {
                addAccount(generateUniqueCardNumber(), pass.getText(), nameField.getText() ,0);
                currentAccount=accounts[accountCnt-1];
                saveAccounts(accounts);
                showInfo("ATM.Account created successfully"+"\nAccount number -> " + formatCardNumber(currentAccount.getCardNumber()));                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(event1 -> {
                    VBox mainMenu = new VBox(30, createDashboardPage());
                    mainMenu.setAlignment(Pos.CENTER);
                    mainRoot.setStyle("-fx-background-color: #1e1e1e;");
                    if (flag)
                    mainRoot.setCenter(mainMenu);
                    else
                        mainRoot.setCenter(createLoginPage());
                });
                pause.play();
            }
        });

        Button backBtn=makeBackBtn(flag?createDashboardPage():createLoginPage());

        box.getChildren().addAll(label, nameField, pass, createAccountBtn,backBtn);
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

    private String formatCardNumber(String cardNumber) {
        return cardNumber.replaceAll("(.{4})(?=.)", "$1-");
    }

    private void addAccount(String cardNumber, String password, String name, double balance) {
        if (accountCnt < accounts.length) {
            accounts[accountCnt++] = new Account(cardNumber, password, name, balance);
        }
    }

    private Account findAccount(String cardNumber, String password) {
        for (Account account : accounts) {
            if (account != null && account.getCardNumber().equals(cardNumber))
                if (password==null || account.getPassword().equals(password)) return account;
        }
        return null;
    }

    private void showAllAccountsPage() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #1e1e1e;");

        Label title = new Label("List of All Accounts");
        title.setFont(Font.font(24));
        title.setStyle("-fx-text-fill: #ffffff;");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #2c2c2c;");

        VBox accountListBox = new VBox(10);
        accountListBox.setPadding(new Insets(10));

        for (Account acc : accounts) {
            if (acc != null) {
                Label accLabel = new Label("Card:    " + formatCardNumber(acc.getCardNumber()) + "\nPassword:    " + acc.getPassword() + "\nBalance:    "  + acc.getBalance() + " $" + "\nName:    " + acc.getName() + "\n\n");
                accLabel.setFont(Font.font(16));
                accLabel.setMaxWidth(1850);
                accLabel.setStyle("-fx-text-fill: #dfe6e9; -fx-background-color: #2d3436; -fx-background-radius: 8; -fx-padding: 8;");
                accountListBox.getChildren().add(accLabel);
            }
        }

        scrollPane.setContent(accountListBox);

        Button backBtn = makeBackBtn(createDashboardPage());

        root.getChildren().addAll(title, scrollPane, backBtn);
        mainRoot.setCenter(root);
    }

    private void cardToCardPage(){
        VBox box=new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #1e1e1e;");

        Label label=new Label("Card to card transfer ");
        label.setStyle("-fx-text-fill: #eeeeee;");
        label.setFont(Font.font(25));

        TextField targetCardField=new TextField();
        targetCardField.setMaxWidth(350);
        targetCardField.setFont(Font.font(22));
        targetCardField.setStyle("-fx-background-radius: 40; -fx-padding: 10;");
        targetCardField.setPromptText("Target card field ");
        targetCardField.setAlignment(Pos.CENTER);

        TextField amountField = new TextField();
        amountField.setPromptText("Amount ($)");
        amountField.setMaxWidth(300);
        amountField.setFont(Font.font(22));
        amountField.setStyle("-fx-background-radius: 40; -fx-padding: 10;");
        amountField.setAlignment(Pos.CENTER);

        setupNavigation(targetCardField,amountField,null);

        Button transferBtn=new Button("TRANSFER");
        transferBtn.setFont(Font.font(20));
        transferBtn.setMaxWidth(300);
        transferBtn.setStyle("-fx-background-color: #3d3dff; -fx-text-fill: #eeeeee; -fx-background-radius: 40;");
        transferBtn.setOnMouseEntered(e -> transferBtn.setStyle("-fx-background-color: #444444; -fx-text-fill: #ffffff; -fx-background-radius: 40;"));
        transferBtn.setOnMouseExited(e -> transferBtn.setStyle("-fx-background-color: #3d3dff; -fx-text-fill: #eeeeee; -fx-background-radius: 40;"));

        transferBtn.setDefaultButton(true);

        transferBtn.setOnKeyPressed(event ->{
            if (event.getCode()==KeyCode.ENTER){
            String targetCard =targetCardField.getText();
            String amountText = amountField.getText();

            if (targetCard.isEmpty() || amountText.isEmpty()){
                showError("Fields cannot be empty!");
                return;
            }
            try {
                double amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    showError("Amount must be greater than 0!");
                    return;
                }
                Account target = findAccount(targetCard, null);

                if (target == null) {
                    showError("Target account not found!");
                    return;
                }

                if (currentAccount.getBalance() < amount) {
                    showError("Insufficient balance!");
                    return;
                }

                if(amount>200){
                    showError("You can't transfer more than 200$");
                    return;
                }

                currentAccount.withdraw(amount);
                target.deposit(amount);
                showInfo("Transferred $" + amount + " to " + targetCard);

                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(event1 -> mainRoot.setCenter(createDashboardPage()));
                pause.play();

            } catch (NumberFormatException ex) {
                showError("Invalid amount!");
            }
          }
        });

        transferBtn.setOnAction(event ->{
            String targetCard =targetCardField.getText();
            String amountText = amountField.getText();

            if (targetCard.isEmpty() || amountText.isEmpty()){
                showError("Fields cannot be empty!");
                return;
            }
            try {
                double amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    showError("Amount must be greater than 0!");
                    return;
                }
                Account target = findAccount(targetCard, null);

                if (target == null) {
                    showError("Target account not found!");
                    return;
                }

                if (currentAccount.getBalance() < amount) {
                    showError("Insufficient balance!");
                    return;
                }

                if(amount>200){
                    showError("You can't transfer more than 200$");
                    return;
                }

                currentAccount.withdraw(amount);
                target.deposit(amount);
                showInfo("Transferred $" + amount + " to " + targetCard);

                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(event1 -> mainRoot.setCenter(createDashboardPage()));
                pause.play();

            } catch (NumberFormatException ex) {
                showError("Invalid amount!");
            }
        });

        Button backBtn = makeBackBtn(createDashboardPage());

        box.getChildren().addAll(label, targetCardField, amountField, transferBtn, backBtn);
        mainRoot.setCenter(box);

    }

    private void changePassword(){
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #1e1e1e;");

        Label label = new Label("Enter old password :");
        label.setFont(Font.font(25));
        label.setStyle("-fx-text-fill: #eeeeee;");

        PasswordField oldPass = new PasswordField();
        oldPass.setPrefSize(130, 70);
        oldPass.setMaxWidth(290);
        oldPass.setPromptText("Old password");
        oldPass.setStyle("-fx-font-size: 20px; -fx-background-radius: 40;");
        oldPass.setAlignment(Pos.CENTER);

        PasswordField newPass = new PasswordField();
        newPass.setPrefSize(130, 70);
        newPass.setMaxWidth(290);
        newPass.setPromptText("New password");
        newPass.setStyle("-fx-font-size: 20px; -fx-background-radius: 40;");
        newPass.setAlignment(Pos.CENTER);

        setupNavigation(oldPass,newPass,null);

        Button change = new Button("CHANGE");
        change.setFont(Font.font(20));
        change.setMaxHeight(60);
        change.setMaxWidth(290);
        change.setStyle("-fx-background-color: #3d3dff; -fx-text-fill: #eeeeee; -fx-background-radius: 40;");
        change.setOnMouseEntered(e -> change.setStyle("-fx-background-color: #444444; -fx-text-fill: #ffffff; -fx-background-radius: 40;"));
        change.setOnMouseExited(e -> change.setStyle("-fx-background-color: #3d3dff; -fx-text-fill: #eeeeee; -fx-background-radius: 40;"));

        change.setDefaultButton(true);

        change.setOnKeyPressed(event -> {
            if(event.getCode()==KeyCode.ENTER){
            if (currentAccount == null) {
                showError("No account is currently logged in!");
                return;
            }

            if (oldPass.getText().isEmpty()) {
                showError("Enter old password!");
            } else if (newPass.getText().isEmpty()) {
                showError("Enter new password!");
            } else if (!currentAccount.getPassword().equals(oldPass.getText())) {
                showError("Old password is incorrect!");
            } else {
                currentAccount.changePass(newPass.getText());
                showInfo("Password changed successfully!");

                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(event1 -> {
                    VBox mainMenu = new VBox(30, createDashboardPage());
                    mainMenu.setAlignment(Pos.CENTER);
                    mainRoot.setStyle("-fx-background-color: #1e1e1e;");
                    mainRoot.setCenter(mainMenu);
                });
                pause.play();
            }
          }
        });

        change.setOnAction(event -> {
            if (currentAccount == null) {
                showError("No account is currently logged in!");
                return;
            }

            if (oldPass.getText().isEmpty()) {
                showError("Enter old password!");
            } else if (newPass.getText().isEmpty()) {
                showError("Enter new password!");
            } else if (!currentAccount.getPassword().equals(oldPass.getText())) {
                showError("Old password is incorrect!");
            } else {
                currentAccount.changePass(newPass.getText());
                showInfo("Password changed successfully!");

                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(event1 -> {
                    VBox mainMenu = new VBox(30, createDashboardPage());
                    mainMenu.setAlignment(Pos.CENTER);
                    mainRoot.setStyle("-fx-background-color: #1e1e1e;");
                    mainRoot.setCenter(mainMenu);
                });
                pause.play();
            }
        });
        Button backBtn=makeBackBtn(createDashboardPage());

        box.getChildren().addAll(label,oldPass,newPass,change,backBtn);
        mainRoot.setStyle("-fx-background-color: #1e1e1e;");
        mainRoot.setCenter(box);
    }

    private ExchangeRate findRateByDate(String date){
        for (ExchangeRate rate:exchangeRates){
            if (rate.getDate().equals(date))
                return rate;
        }
        return null;
    }

    private void currencyExchange(){
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #1e1e1e;");

        Label title = new Label("Currency converter ");
        title.setFont(Font.font(25));
        title.setStyle("-fx-text-fill: #eeeeee;");

        ComboBox<String> dateCombo=new ComboBox<>();
        for (ExchangeRate rate:exchangeRates){
            dateCombo.getItems().add(rate.getDate());
        }
        dateCombo.setPromptText("Select a date:");
        dateCombo.setMaxWidth(290);
        dateCombo.setPrefSize(130, 60);
        dateCombo.setStyle("-fx-font-size: 20px; -fx-background-radius: 40;");

        ComboBox<String> modeCombo =new ComboBox<>();
        modeCombo.getItems().addAll("Rial to Dollar","Dollar to Rial");
        modeCombo.setPromptText("Select mode");
        modeCombo.setMaxWidth(290);
        modeCombo.setPrefSize(130, 60);
        modeCombo.setStyle("-fx-font-size: 20px; -fx-background-radius: 40;");

        TextField inputField=new TextField();
        inputField.setPromptText("Enter amount");
        inputField.setMaxWidth(290);
        inputField.setPrefSize(130, 60);
        inputField.setStyle("-fx-font-size: 24px; -fx-background-radius: 40;");
        inputField.setAlignment(Pos.CENTER);

        Label resultLabel=new Label("0");
        resultLabel.setFont(Font.font(24));
        resultLabel.setStyle("-fx-text-fill: #00ff00;");

        Button convertBtn=new Button("CONVERT");
        convertBtn.setFont(Font.font(20));
        convertBtn.setMaxHeight(60);
        convertBtn.setMaxWidth(290);
        convertBtn.setStyle("-fx-background-color: #3d3dff; -fx-text-fill: #eeeeee; -fx-background-radius: 40;");
        convertBtn.setOnMouseEntered(e -> convertBtn.setStyle("-fx-background-color: #444444; -fx-text-fill: #ffffff; -fx-background-radius: 40;"));
        convertBtn.setOnMouseExited(e -> convertBtn.setStyle("-fx-background-color: #3d3dff; -fx-text-fill: #eeeeee; -fx-background-radius: 40;"));

        convertBtn.setDefaultButton(true);

        convertBtn.setOnKeyPressed(event ->{
            if (event.getCode()==KeyCode.ENTER){
            String selectedDate=dateCombo.getValue();
            String mode=modeCombo.getValue();
            String amountText =inputField.getText();

            if (selectedDate==null || mode==null || amountText==null){
                showError("Please fill all fields!");
                return;
            }

            ExchangeRate selectedRate=findRateByDate(selectedDate);

            if (selectedRate==null){
                showError("Rate not found for selected date!");
                return;
            }

            try {
                double amount=Double.parseDouble(amountText);
                double result;

                if (mode.equals("Dollar to Rial")){
                    result=amount*selectedRate.getRate();
                    resultLabel.setText(amount + " $ = " + result + " Rial");
                } else {
                    result=amount/selectedRate.getRate();
                    resultLabel.setText(amount + " Rial = " + result + " $");
                }
            } catch (NumberFormatException ex){
                showError("Invalid amount");
            }
          }
        });

        convertBtn.setOnAction(event ->{
            String selectedDate=dateCombo.getValue();
            String mode=modeCombo.getValue();
            String amountText =inputField.getText();

            if (selectedDate==null || mode==null || amountText==null){
                showError("Please fill all fields!");
                return;
            }

            ExchangeRate selectedRate=findRateByDate(selectedDate);

            if (selectedRate==null){
                showError("Rate not found for selected date!");
                return;
            }

            try {
                double amount=Double.parseDouble(amountText);
                double result;

                if (mode.equals("Dollar to Rial")){
                    result=amount*selectedRate.getRate();
                    resultLabel.setText(amount + " $ = " + result + " Rial");
                } else {
                    result=amount/selectedRate.getRate();
                    resultLabel.setText(amount + " Rial = " + result + " $");
                }
            } catch (NumberFormatException ex){
                showError("Invalid amount");
            }
        });
        Button backBtn=makeBackBtn(createDashboardPage());

        box.getChildren().addAll(title, dateCombo, modeCombo, inputField, resultLabel, convertBtn, backBtn);
        mainRoot.setCenter(box);
    }

    private VBox createLoginPage() {

        VBox loginBox = new VBox(20);
        loginBox.setPrefHeight(800);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPadding(new Insets(50));
        loginBox.setStyle("-fx-background-color: #1e1e1e;");

        Label title = new Label("Welcome to SmartBank");
        title.setFont(Font.font( 36));
        title.setStyle("-fx-text-fill: #eeeeee;");

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
        cardInput.setStyle("-fx-background-radius: 30; -fx-padding: 10;");
        cardInput.setAlignment(Pos.CENTER);

        PasswordField passInput = new PasswordField();
        passInput.setPromptText("Password");
        passInput.setFont(Font.font(18));
        passInput.setMaxWidth(300);
        passInput.setStyle("-fx-background-radius: 30; -fx-padding: 10;");
        passInput.setAlignment(Pos.CENTER);

        setupNavigation(cardInput,passInput,null);

        Button loginBtn = new Button("Login");
        loginBtn.setFont(Font.font(20));
        loginBtn.setMaxWidth(300);
        loginBtn.setStyle("-fx-background-color: #3d3dff; -fx-text-fill: #eeeeee; -fx-background-radius: 40;");
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle("-fx-background-color: #444444; -fx-text-fill: #ffffff; -fx-background-radius: 40;"));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle("-fx-background-color: #3d3dff; -fx-text-fill: #eeeeee; -fx-background-radius: 40;"));

        loginBtn.setDefaultButton(true);

        loginBtn.setOnKeyPressed(e->{
            if (e.getCode()==KeyCode.ENTER){
                currentAccount = findAccount(cardInput.getText(), passInput.getText());
                if (currentAccount != null) {
                    showInfo("Login successful!");
                    mainRoot.setCenter(createDashboardPage());
                } else {
                    showError("Login failed!");
                }
            }
        });
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
        createAccountBtn.setFont(Font.font(20));
        createAccountBtn.setMaxWidth(300);
        createAccountBtn.setStyle("-fx-background-color: #3d3dff; -fx-text-fill: #eeeeee; -fx-background-radius: 40;");
        createAccountBtn.setOnMouseEntered(e -> createAccountBtn.setStyle("-fx-background-color: #444444; -fx-text-fill: #ffffff; -fx-background-radius: 40;"));
        createAccountBtn.setOnMouseExited(e -> createAccountBtn.setStyle("-fx-background-color: #3d3dff; -fx-text-fill: #eeeeee; -fx-background-radius: 40;"));
        createAccountBtn.setOnAction(event -> newAccountPage(false));

        loginBox.getChildren().addAll(title, cardInput, passInput, loginBtn,createAccountBtn);

        return loginBox;
    }

    private VBox createDashboardPage(){
        VBox vBox=new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-background-color: #1e1e1e;");

        HBox dashboard=new HBox();
        dashboard.setAlignment(Pos.CENTER);

        VBox dashboardLeft =new VBox(20);
        dashboardLeft.setAlignment(Pos.CENTER);
        dashboardLeft.setPadding(new Insets(40));
        dashboardLeft.setStyle("-fx-background-color: #1e1e1e;");

        VBox dashboardRight =new VBox(20);
        dashboardRight.setAlignment(Pos.CENTER);
        dashboardRight.setPadding(new Insets(40));
        dashboardRight.setStyle("-fx-background-color: #1e1e1e;");

        Label welcome=new Label("Welcome : "+currentAccount.getName());
        welcome.setFont(Font.font(28));
        welcome.setStyle("-fx-text-fill: #eeeeee;");

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

        Button depositBtn=makeDashboardBtn("DEPOSIT AMOUNT");
        Button withdrawBtn=makeDashboardBtn("WITHDRAW AMOUNT");
        Button balanceEnquiryBtn=makeDashboardBtn("BALANCE ENQUIRY");
        Button deleteAccountBtn=makeDashboardBtn("DELETE ACCOUNT");
        Button newAccountBtn=makeDashboardBtn("NEW ACCOUNT");
        Button showAllAccountsBtn=makeDashboardBtn("SHOW ALL ACCOUNTS");
        Button cardToCardBtn=makeDashboardBtn("CARD TO CARD");
        Button currencyExchangeBtn =makeDashboardBtn("CURRENCY EXCHANGE");
        Button changePassBtn =makeDashboardBtn("CHANGE PASSWORD");
        Button exitBtn=makeDashboardBtn("EXIT ");

        depositBtn.setOnAction(event -> depositAmountPage());
        withdrawBtn.setOnAction(event -> withdrawAmountPage());
        balanceEnquiryBtn.setOnAction(event -> balanceEnquiryPage());
        deleteAccountBtn.setOnAction(event -> deleteAccountPage());
        newAccountBtn.setOnAction(event -> newAccountPage(true));
        showAllAccountsBtn.setOnAction(event -> showAllAccountsPage());
        cardToCardBtn.setOnAction(event -> cardToCardPage());
        currencyExchangeBtn.setOnAction(event -> currencyExchange());
        changePassBtn.setOnAction(event -> changePassword());
        exitBtn.setOnAction(event -> primaryStage.close());

        dashboardLeft.getChildren().addAll(depositBtn,withdrawBtn,balanceEnquiryBtn,deleteAccountBtn,newAccountBtn);
        dashboardRight.getChildren().addAll(showAllAccountsBtn,cardToCardBtn, currencyExchangeBtn,changePassBtn,exitBtn);

        dashboard.getChildren().addAll(dashboardLeft, dashboardRight);

        vBox.getChildren().addAll(welcome,dashboard);

        return vBox;
    }

    private Button makeDashboardBtn(String text){
        Button button=new Button(text);
        button.setFont(Font.font(20));
        button.setPrefSize(250,60);
        button.setStyle("-fx-background-color: #3d3dff; -fx-text-fill: #eeeeee; -fx-background-radius: 10;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #444444; -fx-text-fill: #ffffff; -fx-background-radius: 10;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #3d3dff; -fx-text-fill: #eeeeee; -fx-background-radius: 10;"));

        return button;
    }

    private Button makeBackBtn(VBox page){
        Button button=new Button("BACK");
        button.setFont(Font.font(15));
        button.setStyle("-fx-background-color: #336633; -fx-text-fill: #eeeeee; -fx-background-radius: 8;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #dfe6e9; -fx-text-fill: black; -fx-background-radius: 8;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #336633; -fx-text-fill: #eeeeee; -fx-background-radius: 8;"));
        button.setOnAction(event -> mainRoot.setCenter(page));

        return button;
    }

    private void setupNavigation(TextInputControl current,TextInputControl next,TextInputControl prev){
        current.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DOWN && next!=null){
                next.requestFocus();
                setupNavigation(next,prev,current);
            } else if (e.getCode() == KeyCode.UP && prev!=null){
                prev.requestFocus();
            }
        });
    }

    @Override
    public void start(Stage stage) throws Exception {

        accounts=loadAccounts();

        primaryStage=stage;

        mainRoot=new BorderPane();
        mainRoot.setCenter(createLoginPage());

        Scene scene=new Scene(mainRoot,525,800);
        stage.setScene(scene);
        stage.setTitle("BANK");
        stage.show();
    }

    public static void main(String[] args) {launch(args);}

}