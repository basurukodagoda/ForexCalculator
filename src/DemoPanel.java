import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;


public class DemoPanel extends JFrame{
    private JTextField txtAmount;
    private JTextField txtFrom;
    private JTextField txtTo;
    private JButton conButton;
    private JPanel panelMain;


    public boolean isAlpha(String name) {                                                               //Check if a string only contains letters
        return name.matches("[a-zA-Z]+");
    }


    public float[] callAPI(String link1) throws IOException, InterruptedException {                     //The API link is sent to this method to be called upon and return ex-rate and ex-value

        String POSTS_API_URL = link1;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("apikey","wGrxfuxFPLZVvYFp4MX0pV4tieBYxpPb")
                .uri(URI.create(POSTS_API_URL))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());     //Collect the API response to a string variable

        String apiMessage = response.body().toString();

        List<String> lines = new ArrayList<>();                                                         //The API message has several lines. Put each of these lines into an array
        apiMessage.lines().forEach(s -> lines.add(s));

        String exRateSt = lines.get(9).replaceAll(" ","");                              //Get the ex-rate & ex-value from this list (as strings)
        String exValueSt = lines.get(12).replaceAll(" ","");

        exRateSt = exRateSt.replace("\"rate\":","");                                    //Get only the value needed
        float exRateFl = Float.parseFloat(exRateSt);

        exValueSt = exValueSt.replace("\"result\":","");
        float exValueFl = Float.parseFloat(exValueSt);

        float[] finalValues = new float[] {exRateFl,exValueFl};                                         //Put the 2 values into an array

        return finalValues;
    }


    public DemoPanel() {
        conButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                float amountfl = 0.0F;                                                                                          //Determine variables to catch inputs
                String fromValue;
                String toValue;
                Boolean ifValidInputs = true;
                String initialURL = "https://api.apilayer.com/exchangerates_data/convert?to=TOVAL&from=FROMVAL&amount=AMOUNT";  // TOVAL , FROMVAL , AMOUNT


                try {                                                                                                           //Check if the amount is a float
                    amountfl = Float.valueOf(txtAmount.getText());
                    }
                catch (Exception txtAmount) {
                    JOptionPane.showMessageDialog(conButton,"Please enter a valid 'Amount' without spaces");
                    ifValidInputs = false;
                    }


                fromValue = txtFrom.getText();
                toValue = txtTo.getText();

                if (((3 == fromValue.length()) && (3 == toValue.length())) && (isAlpha(fromValue + toValue)) ) {          //To and From currency names must have 3 letters only

                    fromValue = fromValue.toUpperCase();
                    toValue = toValue.toUpperCase();
                }
                else {
                    JOptionPane.showMessageDialog(conButton, "Please enter a valid 'From' & 'To' value with 3 letters without spaces");
                    ifValidInputs = false;
                }


                if (ifValidInputs) {

                    String newURL = initialURL.replace("TOVAL",toValue);
                    newURL = newURL.replace("FROMVAL",fromValue);
                    newURL = newURL.replace("AMOUNT",Float.toString(amountfl));                                         //Create the proper UrL

                    try {
                        float[] newArray = callAPI(newURL);                                                                   //Send the link to the above method and get values

                        DecimalFormat df = new DecimalFormat("00.00");                                                 //To round the numbers into 2 decimal places
                        JOptionPane.showMessageDialog(conButton,"Exchange rate: " + df.format(newArray[0]) + "\n" + "Exchange value: " + df.format(newArray[1]));

                    }
                    catch (Exception newArray) {

                        JOptionPane.showMessageDialog(conButton,"ERRORR!!!");
                    }

                }

            }
        });
    }



    public static void main(String[] args) {                                                                                  //Execute the panel
        DemoPanel h = new DemoPanel();
        h.setContentPane(h.panelMain);
        h.setTitle("Currency Converter");
        h.setSize(350,250);
        h.setVisible(true);
        h.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}
