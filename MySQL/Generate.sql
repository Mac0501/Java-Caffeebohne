CREATE TABLE IF NOT EXISTS `room` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL,
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `course` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL,
  `room_id` INT NOT NULL,
  PRIMARY KEY (`id`, `room_id`),
  INDEX `fk_course_room1_idx` (`room_id` ASC) VISIBLE,
  CONSTRAINT `fk_course_room1`
    FOREIGN KEY (`room_id`)
    REFERENCES `room` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `company` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL,
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `student` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL,
  `surname` VARCHAR(255) NULL,
  `javaskills` INT NULL,
  `course_id` INT NOT NULL,
  `Company_id` INT NOT NULL,
  PRIMARY KEY (`id`, `course_id`, `Company_id`),
  INDEX `fk_student_course_idx` (`course_id` ASC) VISIBLE,
  INDEX `fk_student_Company1_idx` (`Company_id` ASC) VISIBLE,
  CONSTRAINT `fk_student_course`
    FOREIGN KEY (`course_id`)
    REFERENCES `course` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_student_Company1`
    FOREIGN KEY (`Company_id`)
    REFERENCES `company` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = InnoDB;
