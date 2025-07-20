package com.store.bookstore.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.store.bookstore.dto.category.CategoryDto;
import com.store.bookstore.dto.category.CategoryRequestDto;
import com.store.bookstore.exception.EntityNotFoundException;
import com.store.bookstore.mapper.CategoryMapper;
import com.store.bookstore.models.Category;
import com.store.bookstore.repository.category.CategoryRepository;
import com.store.bookstore.services.category.CategoryServiceImpl;
import com.store.bookstore.util.CategoryUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    private static final Long CATEGORY_ID = 1L;

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryDto categoryDto;
    private CategoryRequestDto requestDto;
    private CategoryRequestDto invalidRequestDto;
    private Pageable pageable;

    @BeforeEach
    public void setUp() {
        category = CategoryUtil
                .getCategory();
        categoryDto = CategoryUtil.getCategoryDto();
        requestDto = CategoryUtil.getCategoryRequestDto();
        invalidRequestDto = CategoryUtil.getInvalidCategoryRequestDto();
        pageable = CategoryUtil.getPageable();
    }

    @Test
    @DisplayName("Should save category and return corresponding response dto")
    void save_saveCorrectCategory_ShouldSaveCategory() {
        when(categoryMapper.toModel(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto actual = categoryService.save(requestDto);

        assertEquals(categoryDto, actual);
    }

    @Test
    @DisplayName("Should throw an error if a request field name is blank")
    void save_saveNullValuesCategory_ShouldThrowError() {
        String expected = "Null value for field: name";
        when(categoryMapper.toModel(invalidRequestDto))
                .thenThrow(new NullPointerException(expected));

        Exception actual = assertThrows(NullPointerException.class,
                () -> categoryService.save(invalidRequestDto)
        );

        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("Should return paginated list of categories")
    void findAll_searchValidCategories_ShouldReturnCategories() {
        Page<Category> page = new PageImpl<>(List.of(category), pageable, 1);
        when(categoryRepository.findAll(pageable)).thenReturn(page);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        List<CategoryDto> actual = categoryService.findAll(pageable);
        List<CategoryDto> expected = List.of(categoryDto);

        assertEquals(actual, expected);
    }

    @Test
    @DisplayName("Should return a category dto by valid id")
    void getById_SearchByValidId_ShouldReturnBook() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto actual = categoryService.getById(CATEGORY_ID);

        assertEquals(categoryDto, actual);
    }

    @Test
    @DisplayName("Should throw an error when category id is not valid")
    void getById_SearchByInvalidId_ShouldReturnException() {
        Long invalidId = 911L;
        when(categoryRepository.findById(invalidId)).thenReturn(Optional.empty());
        String expected = "Can't find a category by id: " + invalidId;

        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(invalidId));

        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("Should update existed category and return category dto with new parameters")
    void update_UpdateExistedCategory_ShouldUpdateCategory() {
        CategoryRequestDto updateRequestDto =
                new CategoryRequestDto("drama", "story about wildlife");
        CategoryDto expected = new CategoryDto(
                CATEGORY_ID,
                "drama",
                "story about wildlife"
        );
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        doNothing().when(categoryMapper).updateCategoryFromDB(updateRequestDto, category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        CategoryDto actual = categoryService.update(1L, updateRequestDto);

        assertEquals(expected, actual);
        verify(categoryMapper, times(1))
                .updateCategoryFromDB(updateRequestDto, this.category);
    }

    @Test
    @DisplayName("Should throw an error while trying to update a category by an invalid id")
    void update_UpdateWithInvalidId_ShouldThrowError() {
        Long invalidId = 100L;
        CategoryRequestDto updateRequestDto =
                new CategoryRequestDto("drama", "story about wildlife");
        String expected = "Can't find category by id: " + invalidId;
        when(categoryRepository.findById(invalidId)).thenReturn(Optional.empty());

        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(invalidId, updateRequestDto));

        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("Should delete category by valid id")
    void deleteById_DeleteExistedCategory_ShouldDeleteCategory() {
        when(categoryRepository.existsById(CATEGORY_ID)).thenReturn(true);

        categoryService.deleteById(CATEGORY_ID);

        verify(categoryRepository, times(1)).deleteById(CATEGORY_ID);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Should throw an error while trying to delete a category by invalid id")
    void deleteById_DeleteCategoryWithInvalidId_ShouldThrowError() {
        Long invalidId = 100L;
        String expected = "Can't find a category by id: " + invalidId;
        when(categoryRepository.existsById(invalidId)).thenReturn(false);

        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> categoryService.deleteById(invalidId));

        assertEquals(expected, actual.getMessage());
    }
}
