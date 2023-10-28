package com.project_group_5.telegram;
import com.project_group_5.currency.*;
import com.project_group_5.currencyUi.ShowCurr;
import com.project_group_5.telegram.commands.startCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CurrencyTelegramBot extends TelegramLongPollingCommandBot {
    private CurrencyServicePrivate currencyServicePrivate;
    private CurrencyServiceMono currencyServiceMono;
    private CurrencyServiceNBU currencyServiceNBU;
    private ShowCurr showCurr;
    Long chatId;

    public CurrencyTelegramBot() {
        //Инициализация сервисов
        currencyServicePrivate = new PrivatBankCurrencyService();
        currencyServiceMono=new MonoBankCurrencyService();
        currencyServiceNBU=new NBUCurrencyService();
        showCurr = new ShowCurr();
        register(new startCommand());
        startCurrencyUpdates();
    }

    @Override
    public String getBotUsername() {
        return BotConstant.BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BotConstant.BOT_TOKEN;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        String showCuText;
        String showCuText1;
        String showCuText2;
        chatId = update.getCallbackQuery().getMessage().getChatId();
        String chatIdForMess=Long.toString(chatId);
        if (update.getCallbackQuery().getData().equals("getInfoButton")) {
            SendMessage respMess = new SendMessage();

            //CurrencyPrivate
            CurrencyPrivate currencyPrivate = CurrencyPrivate.valueOf(CurrencyPrivate.USD.name());
            double currancyReate = currencyServicePrivate.getRatePrivate(currencyPrivate);
            showCuText =showCurr.convertPrivate(currancyReate, currencyPrivate);

            //CurrencyMono
            CurrencyMono currencyMono=CurrencyMono.valueOf(CurrencyMono.USD.name());
            double currancyReateMono=currencyServiceMono.getRateMono(currencyMono);
            showCuText1=showCurr.convertMono(currancyReateMono,currencyMono);

            //CurrencyNBU
            CurrencyNBU currencyNBU=CurrencyNBU.valueOf(CurrencyNBU.EUR.name());
            double currancyReateNBU=currencyServiceNBU.getRateNBU(currencyNBU);
            showCuText2=showCurr.convertNBU(currancyReateNBU,currencyNBU);

            String result=showCuText+"\n"+showCuText1+"\n"+showCuText2;

            respMess.setText(result);
            respMess.setChatId(chatIdForMess);
            try {
                execute(respMess);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }

        if (update.getCallbackQuery().getData().equals("settings")) {
            SendMessage settingsMess = new SendMessage();
            InlineKeyboardButton getNumber_of_signs = InlineKeyboardButton
                    .builder()
                    .text("Кількість знаків після коми")
                    .callbackData("Number_of_signs")
                    .build();
            InlineKeyboardButton getBank = InlineKeyboardButton
                    .builder()
                    .text("Банк")
                    .callbackData("Bank")
                    .build();
            InlineKeyboardButton getCurrency = InlineKeyboardButton
                    .builder()
                    .text("Валюти")
                    .callbackData("Currency")
                    .build();
            InlineKeyboardButton getNotific = InlineKeyboardButton
                    .builder()
                    .text("Час оповіщень")
                    .callbackData("Notific")
                    .build();

            List<InlineKeyboardButton> buttonsOfSettings = Stream.of(getNumber_of_signs, getBank, getCurrency, getNotific)
                    .map(it -> InlineKeyboardButton.builder().text(it.getText()).callbackData(it.getCallbackData()).build())
                    .collect(Collectors.toList());

            InlineKeyboardMarkup keyboardSettings = InlineKeyboardMarkup
                    .builder()
                    .keyboard(Collections.singleton(buttonsOfSettings))
                    .build();
            showCuText = "Налаштування";
            settingsMess.setText(showCuText);
            settingsMess.setReplyMarkup(keyboardSettings);
            settingsMess.setChatId(chatIdForMess);
            try {
                execute(settingsMess);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

        }

        if (update.getCallbackQuery().getData().equals("Number_of_signs")) {
            SendMessage singsMess = new SendMessage();
            InlineKeyboardButton sings_2 = InlineKeyboardButton
                    .builder()
                    .text("-2")
                    .callbackData("2_sings")
                    .build();
            InlineKeyboardButton sings_3 = InlineKeyboardButton
                    .builder()
                    .text("-3")
                    .callbackData("3_sings")
                    .build();
            InlineKeyboardButton sings_4 = InlineKeyboardButton
                    .builder()
                    .text("-4")
                    .callbackData("4_sings")
                    .build();
            List<InlineKeyboardButton> buttonsOfSings = Stream.of(sings_2, sings_3, sings_4)
                    .map(it -> InlineKeyboardButton.builder().text(it.getText()).callbackData(it.getCallbackData()).build())
                    .collect(Collectors.toList());

            InlineKeyboardMarkup keyboardSings = InlineKeyboardMarkup
                    .builder()
                    .keyboard(Collections.singleton(buttonsOfSings))
                    .build();
            showCuText = "Кількість знаків після коми";
            singsMess.setText(showCuText);
            singsMess.setReplyMarkup(keyboardSings);
            singsMess.setChatId(chatIdForMess);
            try {
                execute(singsMess);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
        //TODO Переделать на валюты выбора какого банка?
        if (update.getCallbackQuery().getData().equals("Currency")) {
            SendMessage currencyMess = new SendMessage();
            List<InlineKeyboardButton> buttonsCurrency = Stream.of(CurrencyPrivate.EUR, CurrencyPrivate.USD)
                    .map(Enum::name)
                    .map(it -> InlineKeyboardButton.builder().text(it).callbackData(it).build())
                    .collect(Collectors.toList());
            InlineKeyboardMarkup keyboardCurrency = InlineKeyboardMarkup
                    .builder()
                    .keyboard(Collections.singleton(buttonsCurrency))
                    .build();
            showCuText = "Валюти";
            currencyMess.setText(showCuText);
            currencyMess.setReplyMarkup(keyboardCurrency);
            currencyMess.setChatId(chatIdForMess);
            try {
                execute(currencyMess);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
        if (update.getCallbackQuery().getData().equals("Bank")) {
            SendMessage bankMess = new SendMessage();
            InlineKeyboardButton privat = InlineKeyboardButton
                    .builder()
                    .text("Приват Банк")
                    .callbackData("privat")
                    .build();
            InlineKeyboardButton mono = InlineKeyboardButton
                    .builder()
                    .text("Моно Банк")
                    .callbackData("mono")
                    .build();
            InlineKeyboardButton nbu = InlineKeyboardButton
                    .builder()
                    .text("НБУ")
                    .callbackData("nbu")
                    .build();
            List<InlineKeyboardButton> buttonsOfBanks = Stream.of(privat, mono, nbu)
                    .map(it -> InlineKeyboardButton.builder().text(it.getText()).callbackData(it.getCallbackData()).build())
                    .collect(Collectors.toList());

            InlineKeyboardMarkup keyboardBanks = InlineKeyboardMarkup
                    .builder()
                    .keyboard(Collections.singleton(buttonsOfBanks))
                    .build();

            showCuText = "Банки";
            bankMess.setText(showCuText);
            bankMess.setReplyMarkup(keyboardBanks);
            bankMess.setChatId(chatIdForMess);
            try {
                execute(bankMess);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

        }

        if (update.getCallbackQuery().getData().equals("Notific")) {
            SendMessage notificMess = new SendMessage();

            ReplyKeyboardMarkup keyboardMarkup=new ReplyKeyboardMarkup();
            List<KeyboardRow> keyNotific=new ArrayList<>();

            KeyboardRow row=new KeyboardRow();
            row.add("9");
            row.add("10");
            row.add("11");
            keyNotific.add(row);

            row=new KeyboardRow();
            row.add("12");
            row.add("13");
            row.add("14");
            keyNotific.add(row);

            row=new KeyboardRow();
            row.add("15");
            row.add("16");
            row.add("17");
            keyNotific.add(row);

            row=new KeyboardRow();
            row.add("18");
            row.add("Вимкнути повідомлення");
            keyNotific.add(row);

            keyboardMarkup.setKeyboard(keyNotific);

            showCuText = "Расписание";
            notificMess.setText(showCuText);
            notificMess.setReplyMarkup(keyboardMarkup);
            notificMess.setChatId(chatIdForMess);
            try {
                execute(notificMess);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
private void startCurrencyUpdates() {   
    Timer timer = new Timer();
    int notificationHour = 9; // Установка времени рассылки (9:00 утра) 
    long interval = 60 * 60 * 1000; // Интервал рассылки (1 час) 
    Date firstExecutionTime = calculateFirstExecutionTime(notificationHour);
    timer.scheduleAtFixedRate(new TimerTask() {
       @Override      
        public void run() {
          // В этом методе получаем актуальные курсы и отправляем их подписчикам           
            sendCurrencyUpdateToSubscribers();
       }    }, firstExecutionTime, interval);
}
private Date calculateFirstExecutionTime(int notificationHour) {    
    long currentTime = System.currentTimeMillis();
    long todayNotificationTime = currentTime - (currentTime % 86400000) + (notificationHour * 3600000); // 86400000 миллисекунд в сутках, 3600000 миллисекунд в часе 
    if (todayNotificationTime <= currentTime) {       
        todayNotificationTime += 86400000; // Если указанное время уже прошло сегодня, переносим на завтра 
    }
    return new Date(todayNotificationTime);
}
private void sendCurrencyUpdateToSubscribers() {
        CurrencyPrivate currency = CurrencyPrivate.USD;
    double exchangeRate = currencyServicePrivate.getRatePrivate(currency);//Выбор банка
    // Отправка сообщения с курсом валюты подписчикам     
    SendMessage message = new SendMessage();
    message.setChatId(chatId.toString());   
    message.setText(showCurr.convertPrivate(exchangeRate, currency));
    try {
       execute(message);    
    } catch (TelegramApiException e) {
       e.printStackTrace();    
    }
}}

