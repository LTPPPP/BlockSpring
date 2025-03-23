package blockspring.blockspring.blockchain.config;

import blockspring.blockspring.blockchain.core.Blockchain;
import blockspring.blockspring.blockchain.network.Node;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class BlockchainConfig {

    @Value("${blockchain.difficulty:4}")
    private int difficulty;

    @Value("${blockchain.reward:100}")
    private float minerReward;

    @Bean
    public Blockchain blockchain() {
        return new Blockchain(difficulty);
    }

    @Bean
    public Node node(Blockchain blockchain) {
        return new Node(blockchain);
    }
}