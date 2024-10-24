package infosupport.be;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FileChatMemory implements ChatMemory {

    private final File storageFile;

    public FileChatMemory(String fileName) {
        this.storageFile = new File(fileName);
    }

    @Override
    public synchronized void add(String conversationId, List<Message> messages) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(storageFile, true))) {
            for (Message message : messages) {
                writer.write(conversationId + "|" + serializeMessage(message));
                writer.newLine();
            }
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }

    @Override
    public synchronized List<Message> get(String conversationId, int lastN) {
        List<Message> allMessages = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(storageFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 2);
                if (parts.length == 2 && parts[0].equals(conversationId)) {
                    Message message = deserializeMessage(parts[1]);
                    if (message != null) {
                        allMessages.add(message);
                    }
                }
            }
        } catch (IOException e) {
            log.warn(e.getMessage());
        }

        int fromIndex = Math.max(0, allMessages.size() - lastN);
        return allMessages.subList(fromIndex, allMessages.size());
    }

    @Override
    public synchronized void clear(String conversationId) {
        List<String> remainingLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(storageFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 2);
                if (parts.length == 2 && !parts[0].equals(conversationId)) {
                    remainingLines.add(line);
                }
            }
        } catch (IOException e) {
            log.warn(e.getMessage());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(storageFile, false))) {
            for (String remainingLine : remainingLines) {
                writer.write(remainingLine);
                writer.newLine();
            }
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }

    private String serializeMessage(Message message) {
        // Simple serialization: messageType|content
        return message.getMessageType() + "|" + message.getContent().replace("\n", "\\n");
    }

    private Message deserializeMessage(String serializedMessage) {
        String[] parts = serializedMessage.split("\\|", 2);
        if (parts.length == 2) {
            MessageType messageType = MessageType.valueOf(parts[0]);
            String content = parts[1].replace("\\n", "\n");

            return switch (messageType) {
                case USER -> new UserMessage(content);
                case SYSTEM -> new SystemMessage(content);
                default -> new AssistantMessage(content);
            };
        }
        return null;
    }
}
