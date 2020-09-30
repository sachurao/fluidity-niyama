package cloud.niyama.fluidity.messaging.ondemand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public abstract class MessagingParticipant {
    private static Logger LOG = LoggerFactory.getLogger(MessagingParticipant.class);
    protected final BlockingQueue inbox = new LinkedBlockingQueue();
    private final InstructionPerformer instructionPerformer = new InstructionPerformer();
    private final Executor executor;

    protected MessagingParticipant() {
        this(Executors.newSingleThreadExecutor());
    }

    protected MessagingParticipant(Executor executor) {
        this.executor = executor;
        this.executor.execute(instructionPerformer);
    }


    public <E> void instruct(E e) {
        try {
            inbox.put(e);
        } catch (InterruptedException interruptedException) {
            LOG.warn("Messaging Participant has been interrupted.", interruptedException);
        }
    }

    protected abstract void executeInstruction(Object instruction);

    private class InstructionPerformer implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    Object instruction = inbox.take();
                    executeInstruction(instruction);
                }
            } catch (InterruptedException e) {
                LOG.warn("Messaging Participant has been interrupted.", e);
            }
        }
    }
}
