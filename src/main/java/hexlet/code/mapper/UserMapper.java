package hexlet.code.mapper;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.model.User;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {
    @Autowired
    private BCryptPasswordEncoder encoder;

    @Mapping(target = "passwordDigest", source = "password")
    public abstract User mapCreate(UserCreateDTO dto);

    @Mapping(target = "passwordDigest", ignore = true)
    public abstract void mapUpdate(UserUpdateDTO dto, @MappingTarget User model);

    public abstract UserDTO mapShow(User model);

    @BeforeMapping
    public void encryptCreatePassword(UserCreateDTO dto) {
        var password = dto.getPassword();
        dto.setPassword(encoder.encode(password));
    }

    @BeforeMapping
    public void encryptUpdatePassword(UserUpdateDTO dto, @MappingTarget User model) {
        var password = dto.getPassword();
        if (password != null && password.isPresent()) {
            model.setPasswordDigest(encoder.encode(password.get()));
        }
    }
}
