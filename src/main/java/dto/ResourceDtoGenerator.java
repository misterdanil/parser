package dto;

import java.util.ArrayList;
import java.util.List;

import model.Resource;

public class ResourceDtoGenerator {
	public static List<ResourceDto> createDtos(List<Resource> resources) {
		List<ResourceDto> dtos = new ArrayList<>();
		resources.forEach(resource -> {
			ResourceDto dto = new ResourceDto();
			dto.setName(resource.getName());
			dto.setLink(resource.getLink());
			dto.setPrice(resource.getPrice());
			dto.setType(resource.getType().toString());

			dtos.add(dto);
		});
		return dtos;
	}
}
