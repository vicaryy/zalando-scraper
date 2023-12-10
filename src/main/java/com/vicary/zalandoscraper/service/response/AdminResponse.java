package com.vicary.zalandoscraper.service.response;

import com.vicary.zalandoscraper.api_telegram.api_object.bot.bot_command.BotCommand;
import com.vicary.zalandoscraper.api_telegram.api_request.commands.DeleteMyCommands;
import com.vicary.zalandoscraper.api_telegram.api_request.commands.GetMyCommands;
import com.vicary.zalandoscraper.api_telegram.api_request.commands.SetMyCommands;
import com.vicary.zalandoscraper.api_telegram.service.QuickSender;
import com.vicary.zalandoscraper.api_telegram.service.RequestService;
import com.vicary.zalandoscraper.exception.IllegalInputException;
import com.vicary.zalandoscraper.exception.ZalandoScraperBotException;
import com.vicary.zalandoscraper.thread_local.ActiveUser;
import com.vicary.zalandoscraper.updater.AutoUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminResponse implements Responser {
    private final static Logger logger = LoggerFactory.getLogger(AdminResponse.class);
    private ActiveUser user;
    private final RequestService requestService;
    private final ResponseFacade responseFacade;
    private final QuickSender quickSender;
    private final AutoUpdater autoUpdater;

    public AdminResponse(RequestService requestService, ResponseFacade responseFacade, QuickSender quickSender, AutoUpdater autoUpdater) {
        this.requestService = requestService;
        this.responseFacade = responseFacade;
        this.quickSender = quickSender;
        this.autoUpdater = autoUpdater;
    }

    public void setActiveUser(ActiveUser activeUser) {
        this.user = activeUser;
    }

    public void response() {
        if (user.getText().startsWith("//set premium "))
            setPremium();

        else if (user.getText().startsWith("//set standard "))
            setStandard();

        else if (user.getText().startsWith("//set admin "))
            setAdmin();

        else if (user.getText().startsWith("//set non-admin "))
            setNonAdmin();

        else if (user.getText().startsWith("//set command "))
            setCommand();

        else if (user.getText().startsWith("//remove command "))
            removeCommand();

        else if (user.getText().startsWith("//remove commands all"))
            removeAllCommands();

        else if (user.getText().equals("//update start"))
            updateStart();

        else if (user.getText().equals("//update start once"))
            updateStartOnce();

        else if (user.getText().equals("//update stop"))
            updateStop();

        else if (user.getText().equals("//update state"))
            updateGetState();

        else if (user.getText().equals("//get all"))
            getAllCommands();
    }

    private void getAllCommands() {
        String commands = """
                List of commands:
                
                *Set:*
                //set premium \\+ nick
                //set standard \\+ nick
                //set admin \\+ nick
                //set non\\-admin \\+ nick
                //set command \\+ command
               
                *Remove:*
                //remove command \\+ command
                //remove commands all
                
                *Update:*
                //update start
                //update start once
                //update stop
                //update state
                
                *Get:*
                //get all \\- getting all commands
                """;
        quickSender.message(user.getChatId(), commands, true);
    }

    private void updateStart() {
        try {
            autoUpdater.start();
            quickSender.message(user.getChatId(), "Auto Updater started successfully.", false);
        } catch (ZalandoScraperBotException ex) {
            quickSender.message(user.getChatId(), ex.getMessage(), false);
            logger.info(ex.getLoggerMessage());
        }
    }

    private void updateStartOnce() {
        try {
            autoUpdater.startOnce();
            quickSender.message(user.getChatId(), "Auto Updater Once started successfully.", false);
        } catch (ZalandoScraperBotException ex) {
            quickSender.message(user.getChatId(), ex.getMessage(), false);
            logger.info(ex.getLoggerMessage());
        }
    }

    private void updateStop() {
        try {
            autoUpdater.stop();
            quickSender.message(user.getChatId(), "Auto Updater stopped.", false);
        } catch (ZalandoScraperBotException ex) {
            quickSender.message(user.getChatId(), ex.getMessage(), false);
            logger.info(ex.getLoggerMessage());
        }
    }

    private void updateGetState() {
            quickSender.message(user.getChatId(), "Current state: " + autoUpdater.getCurrentState(), false);
    }

    private void setPremium() {
        String userNick = removePrefix(user.getText());
        if (responseFacade.updateUserToPremiumByNick(userNick))
            quickSender.message(user.getChatId(), String.format("User %s successfully updated to Premium.", userNick), false);
        else
            throw new IllegalInputException(
                    String.format("User %s does not exist.", userNick),
                    String.format("Admin typed invalid user nick %s.", userNick));
    }

    private void setStandard() {
        String userNick = removePrefix(user.getText());
        if (responseFacade.updateUserToStandardByNick(userNick))
            quickSender.message(user.getChatId(), String.format("User %s successfully updated to Standard.", userNick), false);
        else
            throw new IllegalInputException(
                    String.format("User %s does not exist.", userNick),
                    String.format("Admin typed invalid user nick %s.", userNick));
    }

    private void setAdmin() {
        String userNick = removePrefix(user.getText());
        if (responseFacade.updateUserToAdminByNick(userNick))
            quickSender.message(user.getChatId(), String.format("User %s successfully updated to Admin.", userNick), false);
        else
            throw new IllegalInputException(
                    String.format("User %s does not exist.", userNick),
                    String.format("Admin typed invalid user nick %s.", userNick));
    }

    void setNonAdmin() {
        String userNick = removePrefix(user.getText());
        if (responseFacade.updateUserToNonAdminByNick(userNick))
            quickSender.message(user.getChatId(), String.format("User %s successfully updated to Non-Admin.", userNick), false);
        else
            throw new IllegalInputException(
                    String.format("User %s does not exist.", userNick),
                    String.format("Admin typed invalid user nick %s.", userNick));
    }


    private void setCommand() {
        String textWithoutPrefix = removePrefix(user.getText());
        String[] commandAndDescription = textWithoutPrefix.split(":");

        if (commandAndDescription.length < 2 || commandAndDescription[0].isBlank() || commandAndDescription[1].isBlank())
            throw new IllegalInputException(
                    "Invalid command, make sure you are using this pattern: command:description",
                    "Admin typed invalid command.");


        String command = commandAndDescription[0];
        String description = commandAndDescription[1];
        BotCommand botCommand = BotCommand.builder()
                .command(command)
                .description(description)
                .build();

        List<BotCommand> commandList = requestService.sendRequestList(new GetMyCommands());
        commandList.add(botCommand);

        SetMyCommands setMyCommands = new SetMyCommands(commandList);
        try {
            requestService.send(setMyCommands);
            quickSender.message(user.getChatId(), "Successfully add " + command + " command.", false);
        } catch (Exception ex) {
            throw new IllegalInputException(
                    "Something goes wrong, check your command and try again.",
                    "Fail in setting a command - " + command);
        }
    }

    private void removeCommand() {
        String textWithoutPrefix = removePrefix(user.getText());
        if (textWithoutPrefix.isBlank())
            throw new IllegalInputException(
                    "I don't see command.",
                    "Admin don't typed command to remove.");

        String command = textWithoutPrefix.startsWith("/") ? textWithoutPrefix.substring(1) : textWithoutPrefix;
        List<BotCommand> commandList = requestService.sendRequestList(new GetMyCommands());

        for (BotCommand com : commandList) {
            if (com.getCommand().equals(command)) {
                commandList.remove(com);
                SetMyCommands setMyCommands = new SetMyCommands(commandList);
                try {
                    requestService.send(setMyCommands);
                    quickSender.message(user.getChatId(), "Successfully removed " + command + " command.", false);
                } catch (Exception ex) {
                    throw new IllegalInputException(
                            "Something goes wrong.",
                            "Fail to send remove command.");
                }
                return;
            }
        }

        throw new IllegalInputException(
                "Command does not exist.",
                "Admin don't typed command to remove.");
    }

    private void removeAllCommands() {
        requestService.send(new DeleteMyCommands());
        quickSender.message(user.getChatId(), "Successfully removed all commands.", false);
    }


    private String removePrefix(String text) {
        String[] textArray = text.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < textArray.length; i++) {
            if (i > 1)
                sb.append(textArray[i]).append(" ");
        }
        return sb.toString().trim();
    }
}

