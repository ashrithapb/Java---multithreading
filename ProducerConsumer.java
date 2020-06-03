
import java.util.Queue;
import java.util.LinkedList;

class MessageQueueConnector {
	private int maxCount;
	private int messageCount;

	Queue<String> buffer = new LinkedList<>();

	MessageQueueConnector() {
		this.maxCount = 3; // queue size is 3.
		this.messageCount = 0;
	}

	public synchronized void send(String text, int message) {
		while (messageCount == maxCount) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		String str = text + " " + message;
		buffer.add(str); // place message in buffer
		messageCount++; // Increment messageCount
		System.out.println("(\""+text+"\","+message+")"+" is placed in queue buffer.");
		if (messageCount == 1) {
			notifyAll(); // send signal
		}
	}

	public synchronized String receive() {
		while (messageCount == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		String message = buffer.remove(); // remove message from buffer
		messageCount--; // Decrement messageCount
		if (messageCount == maxCount - 1) {
			notifyAll(); // send signal
		}
		return message;
	}
}

class Producer extends Thread {
	private MessageQueueConnector MessageQueue;

	public Producer(MessageQueueConnector q) {
		MessageQueue = q;
	}

	public void run() {
		System.out.println("Producer is sending (\"add\",4).");
		MessageQueue.send("add", 4);

		System.out.println("Producer is sending (\"multiply\",1).");
		MessageQueue.send("multiply", 1);

		System.out.println("Producer is sending (\"multiply\",8).");
		MessageQueue.send("multiply", 8);

		System.out.println("Producer is sending (\"add\",2).");
		MessageQueue.send("add", 2);

		System.out.println("Producer is sending (\"add\",3).");
		MessageQueue.send("add", 3);

		System.out.println("Producer is sending (\"add\",99).");
		MessageQueue.send("add", 99);

		System.out.println("Producer is sending (\"multiply\",53).");
		MessageQueue.send("multiply", 53);

		System.out.println("Producer is sending (\"end\",0).");
		MessageQueue.send("end", 0);

		try {
			sleep((int) (Math.random() * 100));
		} catch (InterruptedException e) {
		}
	}
}

class Consumer extends Thread {
	private MessageQueueConnector MessageQueue;

	public Consumer(MessageQueueConnector q) {
		MessageQueue = q;
	}

	public void run() {
		String str = null;
		int number;
		String[] words = null;
		int res = 0;
		for (int i = 0; i < 10; i++) {
			str = MessageQueue.receive();
			System.out.println("Consumer received : " + str);
			words = str.split(" ");
			number = Integer.parseInt(words[1]);

			if (words[0].equalsIgnoreCase("add")) {
				AddCalculation a = new AddCalculation();
				res = a.add(number);
			} else if (words[0].equalsIgnoreCase("multiply")) {
				MultiplyCalculation m = new MultiplyCalculation();
				res = m.multiply(number);
			} else if (words[0].equalsIgnoreCase("end") && number == 0) {
				System.out.println("------Program terminates here -----------");
				System.exit(0);
			}
			System.out.println("----> Result of " + "(\"" + words[0] + "\"," + number + ") is " + res);
		}
	}
}

class AddCalculation {

	public int add(int i) {
		int sum = 0;
		sum = 10 + i;
		return sum;
	}
}

class MultiplyCalculation {
	public int multiply(int i) {
		int mul = 0;
		mul = 10 * i;
		return mul;
	}
}

public class ProducerConsumer {
	public static void main(String[] args) {
		System.out.println("Program: To perform addition and multiplication operation on integer 10.");
		System.out.println("------------------------------------------------------------------------");
		MessageQueueConnector q = new MessageQueueConnector();
		Producer p1 = new Producer(q);
		Consumer c1 = new Consumer(q);
		p1.start();
		c1.start();
	}
}