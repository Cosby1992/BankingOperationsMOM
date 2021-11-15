# Banking Operations

This project is a fictive bank loan application app, 
requesting loans by using RabbitMQ messaging system.
The system is not entirely finished, but illustrates 
the use of RabbitMQ between different languages.

**Created by Anders and Dmitro**

## To Run
You should have docker and nodejs installed to run this project.

1. First of all you will need to have a running RabbitMQ server running. 
We suggest that you use docker for this. 
In CLI run the command: 

```docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.9-management```

2. After starting the rabbitMQ server, run the main method in the java project ```src/main/java/dk/cosby/cphbusiness/si/bankingoperationsmom/BankingOperationsMomApplication.java```

3. Now we need the fake banks to provide their fictive loan service. Navigate to the folder ```bankservers```.

4. Now navigate to each subfolder and run the CLI command ```npm install```. When
it is done, run the CLI command ```npm start```

5. Navigate to localhost:8080 in your browser and apply for a fake loan

6. You'll be presented with a list of offers that are retrieved from the banks

7. Once you have selected the desirable loan, it will be shown in the __selected__ section

8. Now it is possible to apply for the loan by pressing the button that is presented at the bottom of the page

9. Fill in the information that is required and press __apply__

10. There should now be an option to download the confirmation ```.txt``` file