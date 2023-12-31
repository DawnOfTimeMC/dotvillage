package org.dotvill.entity.player;

import net.minecraft.nbt.CompoundTag;
import org.dotvill.culture.Culture;

import java.util.Map;

public class PlayerKnowledge {

    private Map<Culture, Integer> knowledges;

    public Map<Culture, Integer> getKnowledges() {
        return this.knowledges;
    }

    public void incrementCultureKnowledge(Culture culture, int amount) {
        if (amount > 0) {
            this.knowledges.put(culture, amount);
        } else {
            throw new RuntimeException("Error in culture knowledge incrementation : value should be > 0");
        }
    }

    public void decrementCultureKnowledge(Culture culture, int amount) {
        if (amount < 0) {
            this.knowledges.put(culture, amount);
        } else {
            throw new RuntimeException("Error in culture knowledge decrementation : value should be < 0");
        }
    }

    public void load(CompoundTag tag) {
    }

    public void save(CompoundTag tag) {
        CompoundTag knowledgeTag = new CompoundTag();
        for (Map.Entry<Culture, Integer> entry : knowledges.entrySet()) {
            CompoundTag cultureTag = new CompoundTag();
            cultureTag.putInt(entry.getKey().toString(), entry.getValue());
        }
        tag.put("Knowledge", knowledgeTag);
    }
}
