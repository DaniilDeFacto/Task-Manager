package hexlet.code.service;

import hexlet.code.dto.labels.LabelCreateDTO;
import hexlet.code.dto.labels.LabelUpdateDTO;
import hexlet.code.dto.labels.LabelDTO;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {
    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper labelMapper;

    public List<LabelDTO> getAll() {
        return labelMapper.map(labelRepository.findAll());
    }

    public LabelDTO findById(Long id) {
        var label = labelRepository.findById(id).orElseThrow();
        return labelMapper.map(label);
    }

    public LabelDTO create(LabelCreateDTO labelData) {
        var label = labelMapper.map(labelData);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

    public LabelDTO update(LabelUpdateDTO labelData, Long id) {
        var label = labelRepository.findById(id).orElseThrow();
        labelMapper.map(labelData, label);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

    public void delete(Long id) {
        labelRepository.deleteById(id);
    }
}
