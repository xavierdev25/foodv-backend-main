package com.foodv.backend.domain.port.in.ai;

import com.foodv.backend.domain.model.ai.AiFeedback;

public interface SubmitFeedbackUseCase {

    record SubmitFeedbackCommand(Long userId, Long productId, Boolean liked) {}

    AiFeedback execute(SubmitFeedbackCommand command);
}