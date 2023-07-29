package ru.ptkom.dao;

import org.springframework.stereotype.Component;
import ru.ptkom.model.Operator;
import ru.ptkom.repository.OperatorRepository;

import java.util.List;
import java.util.Set;

@Component
public class OperatorDAO {

    private final OperatorRepository operatorRepository;

    public OperatorDAO(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }



    public List<Operator> getOperators() {
        List<Operator> operators = operatorRepository.findAll();
        if (operators.size() != 0) {
            return operators;
        } else {
            throw new RuntimeException("Unable to get operators list. There is no operators in data base");
        }
    }

    public List<Operator> getOperatorsByIdList(List<Long> ids) {
        List<Operator> operators = operatorRepository.findAllById(ids);
        if (operators.size() != 0) {
            return operators;
        } else {
            StringBuilder builder = new StringBuilder();
            ids.forEach(id -> {builder.append(id).append(" ,");});
            builder.deleteCharAt(builder.length()-1);
            throw new RuntimeException("Unable to get operators with selected id: " + builder);
        }
    }

    public Operator getOperator(Long id) {
        try {
            return operatorRepository.findById(id).orElseThrow(Exception::new);
        } catch (Exception e) {
            throw new RuntimeException("There is no operator with id: " + id);
        }
    }

    public void saveOperator(Operator operator) {
        operatorRepository.save(operator);
    }

    public void deleteOperatorById(Long id) {
        operatorRepository.deleteById(id);
    }

    public void updateOperator(Operator newOperatorData) {
        Operator oldOperatorData = getOperator(newOperatorData.getId());
        updateName(oldOperatorData, newOperatorData);
        oldOperatorData.setLines(newOperatorData.getLines());
        oldOperatorData.setInputDestination(newOperatorData.getInputDestination());
        updateOutputDestination(oldOperatorData, newOperatorData);
        saveOperator(oldOperatorData);
    }

    private void updateName(Operator oldOperatorData, Operator newOperatorData) {
        String newName = newOperatorData.getName();
        if (newName != null && newName != "") {
            oldOperatorData.setName(newName);
        }
    }

    private void updateOutputDestination(Operator oldOperatorData, Operator newOperatorData) {
        Short newOutputDestination = newOperatorData.getOutputDestination();
        if (operatorRepository.findAll().stream()
                .map(Operator::getOutputDestination)
                .filter(newOutputDestination::equals)
                .count() == 0) {
            oldOperatorData.setOutputDestination(newOutputDestination);
        }
    }
}
