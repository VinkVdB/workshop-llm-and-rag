package infosupport.be;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

@Slf4j
public class LoggingAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    /**
     * The name of the advisor. This is used for logging and debugging.
     */
    @Override
    public String getName() {
        return "LoggingAdvisor";
    }

    /**
     * The order in which the advisor is called. Lower values are called first.
     * @see <a href="https://docs.spring.io/spring-ai/reference/api/advisors.html">Spring AI Advisors</a>
     */
    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        System.out.println("Request:\n" + advisedRequest);
        AdvisedResponse response = chain.nextAroundCall(advisedRequest);
        System.out.println("\nResponse:\n" + response);
        return response;
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        System.out.println("Request:\n" + advisedRequest);
        Flux<AdvisedResponse> responses = chain.nextAroundStream(advisedRequest);
        return new MessageAggregator().aggregateAdvisedResponse(responses, aggregatedAdvisedResponse ->
                System.out.println("\nResponse:\n" + aggregatedAdvisedResponse));
    }
}

