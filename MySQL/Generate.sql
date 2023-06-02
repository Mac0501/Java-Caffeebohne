CREATE TABLE IF NOT EXISTS `bambus`.`room` (
  `id` INT NOT NULL,
  `name` VARCHAR(255) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;
CREATE TABLE IF NOT EXISTS `bambus`.`course` (
  `id` INT NOT NULL,
  `name` VARCHAR(255) NULL,
  `room_id` INT NOT NULL,
  PRIMARY KEY (`id`, `room_id`),
  INDEX `fk_course_room1_idx` (`room_id` ASC) VISIBLE,
  CONSTRAINT `fk_course_room1`
    FOREIGN KEY (`room_id`)
    REFERENCES `bambus`.`room` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;
CREATE TABLE IF NOT EXISTS `bambus`.`company` (
  `id` INT NOT NULL,
  `name` VARCHAR(255) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;
CREATE TABLE IF NOT EXISTS `bambus`.`student` (
  `id` INT NOT NULL,
  `name` VARCHAR(255) NULL,
  `sirname` VARCHAR(255) NULL,
  `javaskills` INT NULL,
  `course_id` INT NOT NULL,
  `Company_id` INT NOT NULL,
  PRIMARY KEY (`id`, `course_id`, `Company_id`),
  INDEX `fk_student_course_idx` (`course_id` ASC) VISIBLE,
  INDEX `fk_student_Company1_idx` (`Company_id` ASC) VISIBLE,
  CONSTRAINT `fk_student_course`
    FOREIGN KEY (`course_id`)
    REFERENCES `bambus`.`course` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_student_Company1`
    FOREIGN KEY (`Company_id`)
    REFERENCES `bambus`.`company` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;