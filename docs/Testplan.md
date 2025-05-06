# Testplan

## Upload Image:

### The OCR can recognise the image of the invoice

1.) Upload an image with high contrast (good printed) and without creases

2.) After a few seconds, a window opens, where the user can see the recognised values

#### Change the values of the OCR 
3.) Change a value, e.g. the date to another date in the current month or amount (date=06.05.2025, amount=5.00 and type=Supermarket)       
    -> checks that only correct dates (workday, within the current month and not already uploaded an image on that day) 
    and amount are accepted (not minus and a number)

4.) Go to "eingerechte Rechnungen"

5.) In that table, you should see the uploaded invoice (refund=3.00, status=PENDING, typ=Supermarket date=06.05.2025 and user=yourUserName)

6.) Status should be PENDING

#### Accept the values of the OCR
3.) Don't change the values, only click on the button "Speichern" (date=06.05.2025, amount=5.00 and type=Supermarket)

4.) Go to "eingerechte Rechnungen"

5.) In that table, you should see the uploaded invoice (refund=3.00, status=ACCEPTING, typ=Supermarket date=06.05.2025 and user=yourUserName)

6.) Status should be ACCEPTED

### The OCR can't recognise the image of the invoice
1.) Upload an image that the OCR can't recognise

2.) You should get a separate window for each value to insert the values 
    -> only correct inputs are accepted

3.) After inputting the values, you should get an overview of all your inputs

4.) Here you can change your inputs (date=06.05.2025, amount=5.00 and type=Supermarket)

5.) Click on the button "Speichern"

6.) Go to "eingerechte Rechnungen"

7.) In that table, you should see the uploaded invoice (refund=3.00, status=PENDING, typ=Supermarket date=06.05.2025 and user=yourUserName)

8.) Status should be PENDING



