package mail_protection;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AccountManagerImpl implements MailAccountManager{
    AttemptCounter count = AttemptCounter.getInstance();
    Data data = new Data();

    @Override
    public void registerNewAccount(String email, String password, Person person) throws DuplicateAccountException, IOException {
        String[] registerPerson = new String[]{person.toString() + ", " + email + ", " + password};
        for (String line : data.csvReader()){
            if (line.split(", ")[0].equals(person.getName())) {
                throw new DuplicateAccountException("Аккаунт уже существует!");
            }
        }
        data.csvWriter(Arrays.asList(registerPerson), true);
    }

    @Override
    public void removeAccount(String email, String password) throws IOException, WrongCredentialsException {
        List<String> persons = data.csvReader();
        String control = null;

        for (String i:persons) {
            if (!i.split(", ")[2].equals(email) || !i.split(", ")[3].equals(password)) {
                continue;
            }
            persons.remove(i);
            data.csvWriter(persons, false);
        }
        throw new WrongCredentialsException("Введены неверные данные!");

    }

    @Override
    public boolean hasAccount(String email) throws IOException {
        boolean control = false;
        List<String> persons = data.csvReader();
        for (String person : persons){
            if (!Objects.equals(person.split(", ")[2], email)) {
                continue;
            }
            control = true;
            break;
        }
        return control;
    }

    @Override
    public Person getPerson(String email, String password) throws TooManyLoginAttemptsException, IOException, WrongCredentialsException {
        Person person = null;
        List<String> persons = data.csvReader();
        for (String account : persons){
            if (account.split(", ")[2].equals(email) && account.split(", ")[3].equals(password)){
                person = new Person(account.split(", ")[0], account.split(", ")[1]);
                count.setCount(0);
            }
        }
        if (person == null){
            throw new WrongCredentialsException("Введены неверные данные! Попытка: " + count.Counter());

        }
        return person;
    }

    @Override
    public int numOfAccounts() throws IOException {
        return data.csvReader().size();
    }

}

